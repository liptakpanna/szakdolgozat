import React from "react";
import { Route, Switch, Redirect } from "react-router-dom";
import LoginForm from "./LoginForm";
import IgvBrowser from "./IgvBrowser";
import Home from "./Home";
import UserStore from "./store/UserStore";

export default function Routes() {
  if(localStorage.getItem("isLoggedIn")) {
    return (
      <Switch>
              <Route path="/login" component={LoginForm} />
              <Route path='/igv' component={IgvBrowser} />
              <Route path='/home' component={Home} />
              <Redirect exact from="/" to="home" />
      </Switch>
    );
  } else {
    return (
      <Switch>
              <Route path="/login" component={LoginForm} />
              <Route path='/igv' component={IgvBrowser} />
              <Route path='/home' component={Home} />
              <Redirect exact from="/" to="login" />
      </Switch>
    );
  }
}

