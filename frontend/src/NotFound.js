import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import {checkJwtToken} from './Common';

class NotFound extends React.Component{

    componentDidMount() {
        checkJwtToken();
    }

    render() {
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
            return(<>
                <NavBar/>
                <div className="container">
                    <h2>Not found</h2>
                    <p>Sorry this page is not available. Please select page from the menu.</p>
                </div>
                </>
            );
        }
        else { 
            return(
                <Redirect to="/login" />
            );
        }
    }
}

export default NotFound;