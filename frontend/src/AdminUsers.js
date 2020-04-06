import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import SubmitButton from './SubmitButton';
import Moment from 'moment';
import {checkJwtToken} from './Common';
import Cookie from "js-cookie";

class AdminUsers extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            isLoggedIn: true,
            items: [],
            showError: false,
            errormessage: null
        }
        this.onEditClick = this.onEditClick.bind(this);
    }

    async componentDidMount() {
        this.setState({isLoggedIn: await checkJwtToken()});
        this.getUsers();
    }

    async getUsers() {
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/users/list', {
                method: 'get',
                headers: new Headers({
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    "Authorization": 'Bearer ' + Cookie.get("jwtToken")

                })
            }).catch(error =>  {
                this.setState({errormessage: "Cannot connect to server"})   
                this.setState({showError:true});
                console.log("Cannot connect to server");
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
                else{
                    console.log(result);
                    this.setState({items: result});
                }
            }
        }
        catch(e) {
            this.setState({errormessage: "Cannot connect to server"})   
            this.setState({showError:true});
            console.log("Cannot connect to server. "+ e);
        }
    }

    onEditClick(event, item) {
        this.props.history.push("/user/edit", {item: item, isAdmin: true, origin: "/users"});
    }

    render() {
        Moment.locale('en');
        if(this.state.isLoggedIn) {
            return(
                <div className="container">
                    <NavBar active="users"/>
                    <div className="container table-responsive-lg">
                        <h1>
                            User List
                        </h1>
                        <table className="table table-hover">
                            <thead  style={{backgroundColor: "#e3f2fd"}}>
                                <tr>
                                    <th scope="col">Id</th>
                                    <th scope="col">Username</th>
                                    <th scope="col">Role</th>
                                    <th scope="col">Email</th>
                                    <th scope="col">Updated By</th>
                                    <th scope="col">Updated At</th>
                                    <th scope="col">Created By</th>
                                    <th scope="col">Created At</th>
                                </tr>
                            </thead>
                            <tbody>
                            {this.state.items.map(function(item, index) {
                                return <tr 
                                        onClick={(e) =>this.onEditClick(e, item)}
                                        className="userListItem pointer" 
                                        key={index} 
                                        data-toogle="tooltip" data-placement="top" title="Click to edit user">
                                        <th>{item.id}</th>
                                        <td> {item.username} </td>
                                        <td> {item.role} </td>
                                        <td> {item.email} </td>
                                        <td> {item.updatedBy} </td>
                                        <td> {item.updatedAt ? Moment(item.updatedAt).format("YYYY.MM.DD. HH:mm") : ""} </td>
                                        <td> {item.createdBy} </td>
                                        <td> {item.createdAt ? Moment(item.createdAt).format("YYYY.MM.DD. HH:mm") : ""} </td>
                                    </tr>
                            }, this)}
                            </tbody>
                        </table>
                    </div>
                    <br/>
                    <SubmitButton
                            text='Add User'
                            type='btn-outline-secondary btn-lg'
                            onClick={ () => this.props.history.push('/users/add')}
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

export default AdminUsers;