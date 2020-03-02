import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import SubmitButton from './SubmitButton';

class AdminUsers extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            'items': []
        }
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
                this.setState({'items': result});
            }
        }
        catch(e) {
            console.log(e)
        }
    }

    render() {
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
            return(
                <div className="container">
                    <NavBar/>
                    <div className="usersContainer">
                        <h1>
                            User List
                        </h1>
                        <table id="users" className="userList">
                            <thead className="userListItem">
                                <tr>
                                    <th>
                                        Id
                                    </th>
                                    <th>
                                        Username
                                    </th>
                                    <th>
                                        Role
                                    </th>
                                    <th>
                                        Email
                                    </th>
                                    <th>
                                        Updated By
                                    </th>
                                    <th>
                                        Updated At
                                    </th>
                                    <th>
                                        Created At
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                            {this.state.items.map(function(item, index) {
                                return <tr className="userListItem" key={index}>
                                        <td>
                                            {item.id}
                                        </td>
                                        <td>
                                            {item.username}
                                        </td>
                                        <td>
                                            {item.role}
                                        </td>
                                        <td>
                                            {item.email}
                                        </td>
                                        <td>
                                            {item.updatedBy}
                                        </td>
                                        <td>
                                            {item.updatedAt}
                                        </td>
                                        <td>
                                            {item.createdAt}
                                        </td>
                                    </tr>
                            })}
                            </tbody>
                        </table>

                        <SubmitButton
                            text='New User'
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