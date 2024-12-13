package de.jakobschaefer.htma.graphql

import de.jakobschaefer.htma.HtmaRenderContext
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IProcessableElementTag
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor
import org.thymeleaf.processor.element.IElementTagStructureHandler
import org.thymeleaf.templatemode.TemplateMode

// syntax:
// <variableName> = ~{ <ServiceName> :: <OperationName> }
class HtmaMutationAttributeProcessor(dialectPrefix: String) :
    AbstractAttributeTagProcessor(
        TemplateMode.HTML, dialectPrefix, null, false, "mutation", true, 10_000, true) {
  override fun doProcess(
      context: ITemplateContext,
      tag: IProcessableElementTag,
      attributeName: AttributeName,
      attributeValue: String,
      structureHandler: IElementTagStructureHandler
  ) {
    val htma = HtmaRenderContext.fromContext(context)
    val mutations = GraphQlExpressionHelper.parseGraphQlExpression(attributeValue, context)
      .assignments
      .mapValues { (_, mutationRef) ->
        for ((ref, cacheValue) in htma.graphql.entries) {
          if (ref.serviceName == mutationRef.serviceName && ref.operationName == mutationRef.operationName) {
            return@mapValues cacheValue
          }
        }
        return@mapValues GraphQlResponse(
          success = false,
          data = null,
          errors = emptyList(),
          performed = false
        )
      }
    for (mutation in mutations) {
      structureHandler.setLocalVariable(mutation.key, mutation.value)
    }
  }
}
