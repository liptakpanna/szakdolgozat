import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';

class Home extends React.Component{

    render() {
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
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