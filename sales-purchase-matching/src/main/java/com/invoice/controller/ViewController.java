//package com.invoice.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
///**
// * Controller for serving the web interface views
// */
//@Controller
//public class ViewController {
//
//    /**
//     * Serves the main dashboard view
//     * 
//     * @return the name of the Thymeleaf template to render
//     */
//    @GetMapping("/")
//    public String home() {
//        return "index";
//    }
//    
//    /**
//     * Serves the dashboard view (same as home)
//     * 
//     * @return the name of the Thymeleaf template to render
//     */
//    @GetMapping("/dashboard")
//    public String dashboard() {
//        return "index";
//    }
//}
package com.invoice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for serving the web interface views
 */
@Controller
public class ViewController {

    /**
     * Serves the main dashboard view
     *
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Serves the dashboard view (same as home)
     *
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "index";
    }
    
    /**
     * Serves the notifications view
     *
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/notifications")
    public String notifications() {
        return "notification-list";
    }
    
    /**
     * Serves the invoice notifications view
     *
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/invoice-notifications")
    public String invoiceNotifications() {
        return "notification-dashboard";
    }
}