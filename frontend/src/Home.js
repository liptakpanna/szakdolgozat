import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import {checkJwtToken} from './Common';

class Home extends React.Component{

    componentWillMount() {
        checkJwtToken();
    }

    render() {
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
            return(
                <NavBar active="home"/>
            );
        }
        else { 
            return(
                <Redirect to="/login" />
            );
        }
    }
}

export default Home;