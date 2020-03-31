import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import {checkJwtToken} from './Common';

class Home extends React.Component{
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