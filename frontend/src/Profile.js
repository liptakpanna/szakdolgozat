import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import SubmitButton from './SubmitButton';
import Moment from 'moment';
import {checkJwtToken} from './Common';

class Profile extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            username: '',
            role: '',
            email: '',
            createdAt: null,
            createdBy: '',
            updatedAt: null,
            updatedBy: ''
        }
        this.onEditClick = this.onEditClick.bind(this);
    }

    componentDidMount() {
        checkJwtToken();
        this.getUser();
    }


    async getUser() {
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/users/me?username=' + localStorage.getItem("username"), {
                method: 'get',
                headers: new Headers({
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    "Authorization": 'Bearer ' + localStorage.getItem("jwtToken")

                })
            });

            let result = await response.json();
            if(result){
                console.log(result);
                this.setState({username: result.username});
                this.setState({email: result.email});
                this.setState({role: result.role});
                this.setState({createdAt: result.createdAt});
                this.setState({createdBy: result.createdBy});
                this.setState({updatedAt: result.updatedAt});
                this.setState({updatedBy: result.updatedBy});
            }
        }
        catch(e) {
            console.log(e)
        }
    }

    onEditClick(event) {
        this.props.history.push("/user/edit", { username: this.state.username, role: this.state.role, email: this.state.email, isAdmin: false, origin: "/profile"});
    }

    render() {
        Moment.locale('en');
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
            if(this.state.updatedAt) {
                return(
                    <div className="container">
                        <NavBar/>
                        <div className="container">
                            <h1>
                                User Profile
                            </h1>
                            <div className="container" style={{backgroundColor: "#e3f2fd"}}>
                                <h2>Username: {this.state.username}</h2>
                                <h2>Email: {this.state.email}</h2>
                                <h2>Role: {this.state.role}</h2>
                                <h2>Updated By: {this.state.updatedBy}</h2>
                                <h2>Updated At: {this.state.updatedAt ? Moment(this.state.updatedAt).format("YYYY.MM.DD. HH:mm") : ""}</h2>
                                <h2>Created By: {this.state.createdBy}</h2>
                                <h2>Created At: {this.state.createdAt ? Moment(this.state.createdAt).format("YYYY.MM.DD. HH:mm") : ""}</h2>
                            </div>
                        </div>
                        <br/>
                        <SubmitButton
                                text='Edit'
                                type='btn-outline-secondary btn-lg'
                                onClick={ (e) => this.onEditClick(e)}
                            />
                    </div>
                );
            }
            else {
                return(
                    <div className="container">
                        <NavBar/>
                        <div className="container">
                            <h1>
                                User Profile
                            </h1>
                            <div className="container" style={{backgroundColor: "#e3f2fd"}}>
                                <h2>Username: {this.state.username}</h2>
                                <h2>Email: {this.state.email}</h2>
                                <h2>Role: {this.state.role}</h2>
                                <h2>Created By: {this.state.createdBy}</h2>
                                <h2>Created At: {this.state.createdAt ? Moment(this.state.createdAt).format("YYYY.MM.DD. HH:mm") : ""}</h2>
                            </div>
                        </div>
                        <br/>
                        <SubmitButton
                                text='Edit'
                                type='btn-outline-secondary btn-lg'
                                onClick={ (e) => this.onEditClick(e)}
                            />
                    </div>
                );
            }
        }
        else { 
            return(
                <Redirect to="login" />
            );
        }
    }
}

export default Profile;