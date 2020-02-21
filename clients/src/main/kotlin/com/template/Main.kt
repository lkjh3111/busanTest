//package com.template
//
//import javafx.application.Application
//import javafx.fxml.FXMLLoader
//import javafx.scene.Parent
//import javafx.scene.Scene
//import javafx.stage.Stage
//
//import java.util.ArrayList
//import java.util.Arrays
//
//
//import net.corda.client.rpc.CordaRPCClient
//import net.corda.client.rpc.CordaRPCConnection
//import net.corda.core.messaging.CordaRPCOps
//import net.corda.core.utilities.NetworkHostAndPort
//
//
//class Main : Application() {
//
//    @Override
//    @Throws(Exception::class)
//    //private String ip="";
//    fun start(primaryStage: Stage) {
//
//        val params = getParameters()
//        val list = params.getRaw()
//        System.out.println(list.size())
//        for (each in list) {
//            System.out.println(each)
//        }
//        System.out.println("list=" + list.toString())
//
//
//        val nodeAddress = NetworkHostAndPort.parse(list.get(0))
//        if (list.size() < 2) {
//            throw Exception("the first arg is rpc address e.g. localhost:10006 and the second is role e.g. E, I P")
//        }
//        val role = list.get(1)
//
//        if (Arrays.asList("E", "I", "P").contains(role) === false) {
//            throw Exception("the second arg be either  E (exchange), I (issuer) & P (exchange participant)")
//        }
//
//
//        val username = "user1"
//        val password = "test"
//        System.out.println("NodeAddress=$nodeAddress")
//
//
//        System.out.println("RPC Connecting....")
//        val client = CordaRPCClient(nodeAddress)
//        // System.out.println("2");
//        val connection = client.start(username, password)
//        //System.out.println("3");
//
//        val proxy = connection.proxy
//
//        System.out.println(proxy.networkMapSnapshot().toString())
//
//
//        val fxmlLoader = FXMLLoader(getClass().getResource("main.fxml"))
//        val root = fxmlLoader.load() as Parent
//        val controller = fxmlLoader.getController()
//
//        //turn on
//        controller.init(primaryStage, proxy, role)
//
//        val scene = Scene(root, 600, 450)
//
//        primaryStage.setScene(scene)
//        //turn on
//        primaryStage.setTitle(proxy.nodeInfo().legalIdentities[0].name.toString())
//        primaryStage.show()
//
//
//    }
//
//    companion object {
//
//
//        fun main(args: Array<String>) {
//            launch(args)
//            System.out.println(args.toString())
//
//        }
//    }
//}
