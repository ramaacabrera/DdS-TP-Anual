package agregador.graphQl;

import agregador.graphQl.resolver.HechoQueryResolver;
import agregador.repository.HechoRepositorio;
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

        // 1️⃣ Cargar el schema desde resources/schema.graphqls
        InputStream schemaStream = getClass()
                .getClassLoader()
                .getResourceAsStream("graphQl/hecho_schema.graphqls");

        if (schemaStream == null) {
            throw new RuntimeException("No se encontró schema.graphqls en resources");
        }

        TypeDefinitionRegistry typeRegistry =
                new SchemaParser().parse(new InputStreamReader(schemaStream));

        // 2️⃣ Crear repositorio y service del agregador
        HechoRepositorio hechoRepositorio = new HechoRepositorio();
        HechoConsultaService hechoConsultaService =
                new HechoConsultaService(hechoRepositorio);

        // 3️⃣ Crear resolvers
        HechoQueryResolver hechoQueryResolver =
                new HechoQueryResolver(hechoConsultaService);

        // 4️⃣ Wirear schema + resolvers
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type(TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher("hechos", env ->
                                hechoQueryResolver.hechos(
                                        env.getArgument("filtro"),
                                        env.getArgument("page")
                                )
                        )
                        .dataFetcher("hecho", env ->
                                hechoQueryResolver.hecho(
                                        env.getArgument("id")
                                )
                        )
                )
                .build();

        // 5️⃣ Crear el schema final
        GraphQLSchema schema = new SchemaGenerator()
                .makeExecutableSchema(typeRegistry, runtimeWiring);

        // 6️⃣ Crear GraphQL
        this.graphQL = GraphQL.newGraphQL(schema).build();
    }

    public GraphQL graphQL() {
        return graphQL;
    }
}
