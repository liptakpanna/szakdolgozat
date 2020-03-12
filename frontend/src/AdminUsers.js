import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import SubmitButton from './SubmitButton';

class AdminUsers extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            items: [],
        }
        this.onEditClick = this.onEditClick.bind(this);
    }

    componentDidMount() {
        this.getUsers();
    }


    async getUsers() {
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/users/list', {
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
                this.setState({items: result});
            }
        }
        catch(e) {
            console.log(e)
        }
    }

    onEditClick(event, id, username, role, email) {
        this.props.history.push("/user/edit", {id: id, username: username, role: role, email:email});
    }

    render() {
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
            return(
                <div className="container">
                    <NavBar/>
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
                                    <th scope="col">Created At</th>
                                </tr>
                            </thead>
                            <tbody>
                            {this.state.items.map(function(item, index) {
                                return <tr onClick={(e) =>this.onEditClick(e, item.id, item.username, item.role, item.email)} className="userListItem" key={index} >
                                        <th scope="col">{item.id}</th>
                                        <td> {item.username} </td>
                                        <td> {item.role} </td>
                                        <td> {item.email} </td>
                                        <td> {item.updatedBy} </td>
                                        <td> {item.updatedAt} </td>
                                        <td> {item.createdAt} </td>
                                    </tr>
                            }, this)}
                            </tbody>
                        </table>

                        <SubmitButton
                            text='Add User'
                            type='btn-outline-secondary'
                            onClick={ () => this.props.history.push('/users/add')}
                        />
                    </div>
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

export default AdminUsers;