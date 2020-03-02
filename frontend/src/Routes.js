import React from "react";
import { Route, Switch, Redirect } from "react-router-dom";
import LoginForm from "./LoginForm";
import IgvBrowser from "./IgvBrowser";
import Home from "./Home";
import AdminUsers from "./AdminUsers";
import AdminNewUser from "./AdminNewUser";

export default function Routes() {
  if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
    return (
      <Switch>
              <Route path="/login" component={LoginForm} />
              <Route path='/igv' component={IgvBrowser} />
              <Route path='/home' component={Home} />
              <Route path='/users/add' component={AdminNewUser} />
              <Route path='/users' component={AdminUsers} />
              <Redirect exact from="/" to="home" />
      </Switch>
    );
  } else {
    return (
      <Switch>
              <Route path="/login" component={LoginForm} />
              <Route path='/igv' component={IgvBrowser} />
              <Route path='/home' component={Home} />
              <Route path='/users' component={AdminUsers} />
              <Route path='/users/add' component={AdminNewUser} />
              <Redirect exact from="/" to="login" />
      </Switch>
    );
  }
}

