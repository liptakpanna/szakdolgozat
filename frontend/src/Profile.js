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

    checkIfUpdated() {
        if(this.state.updatedAt) {
            return(
                <div className="col">
                    <p className="card-text">Updated By: {this.state.updatedBy}</p>
                    <p className="card-text">Updated At: {this.state.updatedAt ? Moment(this.state.updatedAt).format("YYYY.MM.DD. HH:mm") : ""}</p>
                    <p className="card-text">Created By: {this.state.createdBy}</p>
                    <p className="card-text">Created At: {this.state.createdAt ? Moment(this.state.createdAt).format("YYYY.MM.DD. HH:mm") : ""}</p>
                </div>
            );
        } else {
            return(
                <div className="col">
                    <p className="card-text">Created By: {this.state.createdBy}</p>
                    <p className="card-text">Created At: {this.state.createdAt ? Moment(this.state.createdAt).format("YYYY.MM.DD. HH:mm") : ""}</p>
                </div>
            );
        }
    }

    render() {
        Moment.locale('en');
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
            return(
                <div className="container">
                    <NavBar/>
                    <div className="container">
                        <div className="card">
                            <h5 className="card-header" style={{backgroundColor: "#e3f2fd"}}>User Profile</h5>
                            <div className="card-body">
                                <div className="row">
                                    <div className="col">
                                        <p className="card-text">Username: {this.state.username}</p>
                                        <p className="card-text">Email: {this.state.email}</p>
                                        <p className="card-text">Role: {this.state.role}</p>
                                    </div>
                                    {this.checkIfUpdated()}
                                </div>
                            </div> 
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
                <Redirect to="login" />
            );
        }
    }
}

export default Profile;