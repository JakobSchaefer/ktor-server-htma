package de.jakobschaefer.htma.examples.basic

import com.apollographql.apollo.ApolloClient
import de.jakobschaefer.htma.Htma
import de.jakobschaefer.htma.graphql.GraphQlEngineApollo
import de.jakobschaefer.htma.graphql.GraphQlEngineJava
import de.jakobschaefer.htma.routing.htma
import graphql.schema.idl.RuntimeWiring
import io.ktor.server.application.*
import io.ktor.server.routing.*
import java.util.*

fun Application.module() {
  var currentName = "World"
  install(Htma) {
    supportedLocales = listOf(Locale.GERMAN, Locale.ENGLISH)
    graphqlServices = mapOf(
      "graphql" to GraphQlEngineJava(
        schemaResourceFile = "/graphql/schema.graphqls",
        runtimeWiring = RuntimeWiring.newRuntimeWiring()
          .type("Query") { builder ->
            builder.dataFetcher("name") { currentName }
              .dataFetcher("greeting") { env ->
                val name = env.getArgument<String>("name") ?: currentName
                "Hello $name! I'm the server."
              }
          }
          .type("Mutation") { builder ->
            builder.dataFetcher("setName") { env ->
              val newName = env.getArgument<String>("name") ?: "World"
              currentName = newName.ifBlank { "World" }
              currentName
            }
          }
          .build()
        ),
      "starwars" to GraphQlEngineApollo(
        apolloClient = ApolloClient.Builder()
          .serverUrl("https://swapi-graphql.netlify.app/.netlify/functions/index")
          .build()
      )
    )
  }

  routing {
    htma {
    }
  }
}
