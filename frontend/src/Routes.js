import React from "react";
import { Route, Switch, Redirect } from "react-router-dom";
import LoginForm from "./pages/LoginPage";
import IgvBrowser from "./pages/alignment/IgvBrowser";
import Home from "./pages/Home";
import AdminUsers from "./pages/user/AdminUsers";
import AdminAddUser from "./pages/user/AdminAddUser";
import EditUser from "./pages/user/EditUser";
import Profile from "./pages/user/Profile";
import Alignments from "./pages/alignment/Alignments";
import CreateAlignment from "./pages/alignment/CreateAlignment";
import EditAlignment from "./pages/alignment/EditAlignment";
import NotFound from "./pages/NotFoundPage";


export default function Routes() {
    let role = localStorage.getItem("role");
    if(role === "ADMIN") {
      return (
        <Switch>
                <Route path="/login" component={LoginForm} />
                <Route path='/alignments/igv' component={IgvBrowser} />
                <Route path='/home' component={Home} />
                <Route path='/users/add' component={AdminAddUser} />
                <Route path='/user/edit' component={EditUser} />
                <Route path='/users' component={AdminUsers} />
                <Route path='/profile' component={Profile} />
                <Route path='/alignments/edit' component={EditAlignment} />
                <Route path='/alignments/add' component={CreateAlignment} />
                <Route path='/alignments' component={Alignments} />
                <Redirect exact from="/" to="home" />
                <Route component={NotFound} />
        </Switch>
      );
    } else if (role === "RESEARCHER") {
      return (
        <Switch>
                <Route path="/login" component={LoginForm} />
                <Route path='/alignments/igv' component={IgvBrowser} />
                <Route path='/home' component={Home} />
                <Route path='/profile' component={Profile} />
                <Route path='/user/edit' component={EditUser} />
                <Route path='/alignments/edit' component={EditAlignment} />
                <Route path='/alignments/add' component={CreateAlignment} />
                <Route path='/alignments' component={Alignments} />
                <Redirect exact from="/" to="home" />
                <Route component={NotFound} />
        </Switch>
      );
    } else if (role === "GUEST") {
      return (
        <Switch>
                <Route path="/login" component={LoginForm} />
                <Route path='/alignments/igv' component={IgvBrowser} />
                <Route path='/home' component={Home} />
                <Route path='/profile' component={Profile} />
                <Route path='/user/edit' component={EditUser} />
                <Route path='/alignments' component={Alignments} />
                <Redirect exact from="/" to="home" />
                <Route component={NotFound} />
        </Switch>
      );
    } else {
      return(
        <Switch>
          <Route path="/login" component={LoginForm} />
          <Route path='/home' component={Home} />
          <Redirect from="*" to="/login" />
        </Switch>
      )
    }
}

