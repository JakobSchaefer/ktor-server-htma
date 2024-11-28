package de.jakobschaefer.htma.graphql

import com.apollographql.apollo.api.Query
import de.jakobschaefer.htma.HtmaRenderContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IProcessableElementTag
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor
import org.thymeleaf.processor.element.IElementTagStructureHandler
import org.thymeleaf.standard.expression.StandardExpressions
import org.thymeleaf.templatemode.TemplateMode
import kotlin.reflect.full.primaryConstructor

// syntax:
// <variableName> = ~{ <ServiceName> :: <QueryName>(parameters...) }
class HtmaQueryAttributeProcessor(dialectPrefix: String) :
    AbstractAttributeTagProcessor(
        TemplateMode.HTML, dialectPrefix, null, false, "query", true, 10_000, true) {
  override fun doProcess(
      context: ITemplateContext,
      tag: IProcessableElementTag,
      attributeName: AttributeName,
      attributeValue: String,
      structureHandler: IElementTagStructureHandler
  ) {
    val htma = HtmaRenderContext.fromContext(context)
    val assignments = getVariableAssignments(attributeValue)
    val queries =
        runBlocking {
              assignments
                  .mapValues { (_, value) -> computeQuery(value, context) }
                  .map { (variableName, queryRef) ->
                    async {
                      val result =
                          if (htma.graphqlCache.containsKey(queryRef)) {
                            htma.graphqlCache[queryRef]
                          } else {
                            val queryClass = Class.forName("de.jakobschaefer.htma.${queryRef.serviceName}.${queryRef.queryName}").kotlin
                            val query = queryClass.primaryConstructor!!.call(*queryRef.queryParameters.toTypedArray()) as Query<*>
                            val engine = htma.graphqlServices[queryRef.serviceName] ?: throw IllegalStateException("Unknown GraphQL service ${queryRef.serviceName}")
                            val response = engine.query(query = query)
                            htma.graphqlCache[queryRef] = response
                            response
                          }
                      variableName to result
                    }
                  }
                  .awaitAll()
            }
            .toMap()

    for (query in queries) {
      structureHandler.setLocalVariable(query.key, query.value)
    }
  }
}

fun getVariableAssignments(str: String): Map<String, String> {
  return str.split(',')
      .map {
        val key = it.substringBefore('=').trim()
        val value = it.substringAfter('=').trim()
        key to value
      }
      .toMap()
}

fun computeQuery(queryString: String, context: ITemplateContext): QueryRef {
  val parser = StandardExpressions.getExpressionParser(context.configuration)
  val fragmentString = queryString.substringAfter("~{").substringBeforeLast('}')
  val serviceName = fragmentString.substringBefore("::").trim()
  val queryExpression = fragmentString.substringAfter("::").trim()
  val hasParameters = queryExpression.contains("(")
  val queryName =
      if (hasParameters) {
        queryExpression.substringBefore('(')
      } else {
        queryExpression
      }
  val queryParameters =
      if (hasParameters) {
        queryExpression.substringAfter('(').substringBeforeLast(')').split(',').map { expr ->
          val variableExpression = parser.parseExpression(context, expr)
          variableExpression.execute(context)
        }
      } else {
        emptyList()
      }
  return QueryRef(
      serviceName = serviceName,
      queryName = queryName,
      queryParameters = queryParameters,
  )
}
