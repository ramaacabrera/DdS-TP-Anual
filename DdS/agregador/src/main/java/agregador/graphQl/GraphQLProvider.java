package agregador.graphQl;

import agregador.graphQl.resolver.ColeccionFieldResolver;
import agregador.graphQl.resolver.ColeccionQueryResolver;
import agregador.graphQl.resolver.HechoQueryResolver;
import agregador.repository.ColeccionRepositorio;
import agregador.repository.HechoRepositorio;
import agregador.service.ColeccionConsultaService;
import agregador.service.HechoConsultaService;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;

import java.io.InputStream;
import java.io.InputStreamReader;

public class GraphQLProvider {

    private GraphQL graphQL;

    public GraphQLProvider() {
        init();
    }

    private void init() {

        //Cargar el schema desde resources/schema.graphqls
        InputStream schemaStream = getClass()
                .getClassLoader()
                .getResourceAsStream("graphQl/hecho_schema.graphqls");

        if (schemaStream == null) {
            throw new RuntimeException("No se encontró schema.graphqls en resources");
        }

        TypeDefinitionRegistry typeRegistry =
                new SchemaParser().parse(new InputStreamReader(schemaStream));


        //Crear repositorio y service del agregador
        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        HechoConsultaService hechoConsultaService =
                new HechoConsultaService(hechoRepositorio);

        ColeccionRepositorio coleccionRepositorio = new ColeccionRepositorio();
        ColeccionConsultaService coleccionService =
                new ColeccionConsultaService(coleccionRepositorio);

        //Crear resolvers
        HechoQueryResolver hechoQueryResolver =
                new HechoQueryResolver(hechoConsultaService);

        ColeccionQueryResolver coleccionResolver =
                new ColeccionQueryResolver(coleccionService);

        ColeccionFieldResolver coleccionFieldResolver =
                new ColeccionFieldResolver(hechoConsultaService, coleccionService);

        //Wirear schema + resolvers
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()

                // QUERIES
                .type(TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher("hechos", env ->
                                hechoQueryResolver.hechos(
                                        env.getArgument("filtro"),
                                        env.getArgument("page")
                                )
                        )
                        .dataFetcher("hecho", env ->
                                hechoQueryResolver.hecho(env.getArgument("id"))
                        )
                        .dataFetcher("colecciones", env ->
                                coleccionResolver.colecciones(
                                        env.getArgument("filtro"),
                                        env.getArgument("page")
                                )
                        )
                        .dataFetcher("coleccion", env ->
                                coleccionResolver.coleccion(env.getArgument("id"))
                        )
                )

                // CAMPOS DE COLECCION
                .type(TypeRuntimeWiring.newTypeWiring("Coleccion")
                        .dataFetcher("hechos", env ->
                                coleccionFieldResolver.hechos(env.getSource())
                        )
                        .dataFetcher("hechosConsensuados", env ->
                                coleccionFieldResolver.hechosConsensuados(env.getSource())
                        )
                        .dataFetcher("fuentes", env ->
                                coleccionFieldResolver.fuentes(env.getSource())
                        )
                )

                .build();

        //Crear el schema final
        GraphQLSchema schema = new SchemaGenerator()
                .makeExecutableSchema(typeRegistry, runtimeWiring);

        //Crear GraphQL
        this.graphQL = GraphQL.newGraphQL(schema).build();
    }

    public GraphQL graphQL() {
        return graphQL;
    }
}
