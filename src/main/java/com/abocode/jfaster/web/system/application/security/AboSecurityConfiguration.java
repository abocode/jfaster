package com.abocode.jfaster.web.system.application.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by Franky Guan on 2017/4/2.
 */
@Configuration
@EnableWebSecurity
public class AboSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        /*auth.inMemoryAuthentication()
                .withUser("user")
                .password("password")
                .roles("USER");*/
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       /* http.requestMatchers()
                .antMatchers("/loginController.do*//**","/repairController.do*//**","/userController.do*//**")
                .and()
                .authorizeRequests()
                .antMatchers("*//**").hasRole("USER").and()
                .httpBasic();*/
    }
    }