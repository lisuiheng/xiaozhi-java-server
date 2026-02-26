package com.github.lisuiheng;

//import io.milvus.client.MilvusServiceClient;
//import io.milvus.param.collection.DropCollectionParam;
//import io.milvus.param.collection.HasCollectionParam;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@Slf4j
//@SpringBootTest(classes = AstraServerApplication.class)
//public class MilvusTest {
//    @Autowired
//    private MilvusServiceClient milvusClient;
//
//    @Test
//    void dropTestRagCollection() {
//        String collectionName = "test_rag_docs";
//        var resp = milvusClient.hasCollection(
//                HasCollectionParam.newBuilder().withCollectionName(collectionName).build()
//        );
//        if (resp.getData()) {
//            milvusClient.dropCollection(
//                    DropCollectionParam.newBuilder().withCollectionName(collectionName).build()
//            );
//            System.out.println("✅ Collection '" + collectionName + "' dropped.");
//        } else {
//            System.out.println("⚠️ Collection '" + collectionName + "' does not exist.");
//        }
//    }
//}
