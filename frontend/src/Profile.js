import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import SubmitButton from './SubmitButton';
import Moment from 'moment';
import {checkJwtToken} from './Common';
import Cookie from "js-cookie";

class Profile extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            isLoggedIn: true,
            item: [],
            showError: false,
            errormessage: null
        }
        this.onEditClick = this.onEditClick.bind(this);
    }

    async componentDidMount() {
        this.setState({isLoggedIn: await checkJwtToken()});
        this.getUser();
    }

    async getUser() {
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/users/me', {
                method: 'get',
                headers: new Headers({
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    "Authorization": 'Bearer ' + Cookie.get("jwtToken")

                })
            })

            let result = await response.json();
            if(result){
                if(result.status === 500) {
                    this.setState({errormessage: result.message})
                    this.setState({showError:true});
                }
                else if(result.status === 403) {
                    this.props.history.push("/login");
                }
                else {
                    console.log(result);
                    this.setState({item: result});
                }
            }
        }
        catch(e) {
            this.setState({errormessage: "Cannot connect to server"})
            this.setState({showError:true});
            console.log("Cannot connect to server. " + e);
        }
    }

    onEditClick(event) {
        this.props.history.push("/user/edit", {item: this.state.item, isAdmin: false, origin: "/profile"});
    }

    checkIfUpdated() {
        if(this.state.item.updatedAt) {
            return(
                <div className="col">
                    <p className="card-text">Updated By: {this.state.item.updatedBy}</p>
                    <p className="card-text">Updated At: {this.state.item.updatedAt ? Moment(this.state.item.updatedAt).format("YYYY.MM.DD. HH:mm") : ""}</p>
                    <p className="card-text">Created By: {this.state.item.createdBy}</p>
                    <p className="card-text">Created At: {this.state.item.createdAt ? Moment(this.state.item.createdAt).format("YYYY.MM.DD. HH:mm") : ""}</p>
                </div>
            );
        } else {
            return(
                <div className="col">
                    <p className="card-text">Created By: {this.state.item.createdBy}</p>
                    <p className="card-text">Created At: {this.state.item.createdAt ? Moment(this.state.item.createdAt).format("YYYY.MM.DD. HH:mm") : ""}</p>
                </div>
            );
        }
    }

    render() {
        Moment.locale('en');
        if(this.state.isLoggedIn) {
            return(
                <div className="container">
                    <NavBar active="profile"/>
                    <div className="container">
                        <div className="card mt-4">
                            <h5 className="card-header" style={{backgroundColor: "#e3f2fd"}}>User Profile</h5>
                            <div className="card-body">
                                <div className="row">
                                    <div className="col">
                                        <p className="card-text">Username: {this.state.item.username}</p>
                                        <p className="card-text">Email: {this.state.item.email}</p>
                                        <p className="card-text">Role: {this.state.item.role}</p>
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
                    {this.state.showError ? <div className="alert alert-primary mt-3" role="alert">{this.state.errormessage}</div> : null }
                </div>
            );
        }
        else { 
            return(
                <Redirect to="/login" />
            );
        }
    }
}

export default Profile;