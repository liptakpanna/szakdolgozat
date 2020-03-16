import React from "react";
import { Route, Switch, Redirect } from "react-router-dom";
import LoginForm from "./LoginForm";
import IgvBrowser from "./IgvBrowser";
import Home from "./Home";
import AdminUsers from "./AdminUsers";
import AdminNewUser from "./AdminNewUser";
import EditUser from "./EditUser";
import Profile from "./Profile";
import Alignments from "./Alignments";
import CreateAlignment from "./CreateAlignment";

export default function Routes() {
  if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
    let role = localStorage.getItem("role");
    if(role === "ADMIN") {
      return (
        <Switch>
                <Route path="/login" component={LoginForm} />
                <Route path='/alignments/igv' component={IgvBrowser} />
                <Route path='/home' component={Home} />
                <Route path='/users/add' component={AdminNewUser} />
                <Route path='/user/edit' component={EditUser} />
                <Route path='/users' component={AdminUsers} />
                <Route path='/profile' component={Profile} />
                <Route path='/alignments/add' component={CreateAlignment} />
                <Route path='/alignments' component={Alignments} />
                <Redirect exact from="/" to="home" />
        </Switch>
      );
    } else if (role === "RESEARCHER") {
      return (
        <Switch>
                <Route path="/login" component={LoginForm} />
                <Route path='/alignments/igv' component={IgvBrowser} />
                <Route path='/home' component={Home} />
                <Route path='/profile' component={Profile} />
                <Route path='/alignments/add' component={CreateAlignment} />
                <Route path='/alignments' component={Alignments} />
                <Redirect exact from="/" to="home" />
        </Switch>
      );
    } else {
      return (
        <Switch>
                <Route path="/login" component={LoginForm} />
                <Route path='/alignments/igv' component={IgvBrowser} />
                <Route path='/home' component={Home} />
                <Route path='/profile' component={Profile} />
                <Route path='/alignments' component={Alignments} />
                <Redirect exact from="/" to="home" />
        </Switch>
      );
    }

  } else {
    return (
      <Switch>
              <Route path="/login" component={LoginForm} />
              <Route path='/igv' component={IgvBrowser} />
              <Route path='/home' component={Home} />
              <Route path='/users' component={AdminUsers} />
              <Route path='/users/add' component={AdminNewUser} />
              <Route path='/user/edit' component={EditUser} />
              <Route path='/profile' component={Profile} />
              <Redirect exact from="/" to="login" />
      </Switch>
    );
  }
}

