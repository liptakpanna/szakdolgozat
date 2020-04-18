import React from 'react';
import NavBar from '../util/NavBar';
import { Redirect } from 'react-router-dom';
import {checkJwtToken} from '../util/Common';

class NotFoundPage extends React.Component{

    constructor(props){
        super(props);
        this.state = {
            isLoggedIn : true,
        }
    }

    async componentDidMount(){
        this.setState({isLoggedIn: await checkJwtToken()});
    }

    render() {
        if(this.state.isLoggedIn) {
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

export default NotFoundPage;