package com.twitter.streaming.config.server.config;

import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/actuator/**");
    super.configure(web);
  }
}

