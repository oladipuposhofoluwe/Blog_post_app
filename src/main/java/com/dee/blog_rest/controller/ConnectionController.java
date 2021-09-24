//package com.dee.blog_rest.controller;
//
//import com.dee.blog_rest.security.CurrentUser;
//import com.dee.blog_rest.security.UserPrincipal;
//import com.dee.blog_rest.entities.User;
//import com.dee.blog_rest.requests_and_responses.ApiResponse;
//import com.dee.blog_rest.requests_and_responses.UsernameDetail;
//import com.dee.blog_rest.services.serviceImplementation.UserServiceImplementation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Logger;
//
//@RestController
//@RequestMapping(path = "/api/")
//public class ConnectionController {
//
//
//    Logger logger = Logger.getLogger(UserController.class.getName());
//
//    private UserServiceImplementation userServiceImplementation;
//
//    @Autowired
//    public ConnectionController(UserServiceImplementation userServiceImplementation) {
//        this.userServiceImplementation = userServiceImplementation;
//    }
//
//    @GetMapping("/allconnections")
//    public List<String> getConnections(@CurrentUser UserPrincipal userPrincipal){
//        User byId = userServiceImplementation.findById(userPrincipal.getId());
//        List<User> connections = byId.getConnections();
//        List<String> list = new ArrayList<>();
//        connections.forEach(user -> {
//            UsernameDetail username = new UsernameDetail();
//            String fullname = user.getId()+" " + user.getFirstName()+" "+user.getLastName();
//            username.setUsername(fullname);
//            list.add(fullname);
//        });
//        return list;
//    }
//
//    @Transactional
//    @GetMapping("/connect/{connection_id}")
//    public ResponseEntity<ApiResponse> connectWithAnotherUser(
//            @PathVariable(name = "connection_id") Long connectionId,
//            @CurrentUser UserPrincipal currentUser){
//        User otherUser = userServiceImplementation.findById(connectionId);
//        if (otherUser!=null){
//            User thisUser = userServiceImplementation.findById(currentUser.getId());
//            List<User> connections = thisUser.getConnections();
//            connections.add(otherUser);
//
////            NumberOfConnections totalConnections = thisUser.getNumberOfConnections();
////            int i = totalConnections.getTotal() + 1;
////            totalConnections.setTotal(i);
////            thisUser.setNumberOfConnections(totalConnections);
//
//
////            List<User> otherUserConnections = otherUser.getConnections();
////            otherUserConnections.add(thisUser);
//            return ResponseEntity.ok(new ApiResponse(Boolean.TRUE,
//                    "You have successfully connected with "+otherUser.getFirstName()+" "+
//                    otherUser.getLastName()));
//        }
//        return ResponseEntity.ok(new ApiResponse(Boolean.FALSE, "Could not connect with "+otherUser.getFirstName()+" "+
//                otherUser.getLastName()));
//    }
//
//
//}
