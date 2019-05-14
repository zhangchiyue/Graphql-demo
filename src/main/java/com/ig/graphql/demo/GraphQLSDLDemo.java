package com.ig.graphql.demo;

import com.ig.graphql.vo.Card;
import com.ig.graphql.vo.User;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class GraphQLSDLDemo {

    public static void main(String[] args) throws IOException {

        // 读取GraphQL文件，进行解析
        String fileName = "user.graphqls";
        String fileContent = IOUtils.toString(GraphQLSDLDemo.class.getClassLoader().getResource(fileName), "UTF-8");
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(fileContent);

        // 解决的是数据的查询
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("UserQuery", builder ->
                        builder.dataFetcher("user", environment -> {
                            Long id = environment.getArgument("id");
                            Card card = new Card("123456789", id);
                            return new User(id, "张三:" + id, 20 + id.intValue(), card);
                        })
                )
                .build();

        // 生成Schema
        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);

        // 根据Schema对象生成GraphQL对象
        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        String query = "{user(id:100){id,name,age,card{cardNumber}}}";
        ExecutionResult result = graphQL.execute(query);

        System.out.println("query:" + query);
        System.out.println(result.toSpecification());

    }
}