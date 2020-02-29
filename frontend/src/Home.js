import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import UserStore from './store/UserStore';

class Home extends React.Component{

    render() {
        if(localStorage.getItem("isLoggedIn")) {
            return(
                <NavBar/>
            );
        }
        else { 
            return(
                <Redirect to="login" />
            );
        }
    }
}

export default Home;