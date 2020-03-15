import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import InputField from './InputField';
import SubmitButton from './SubmitButton';
import PreviousPageIcon from './PreviousPageIcon';

class EditUser extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            username: this.props.location.state.username,
            password: '',
            email: this.props.location.state.email,
            role: this.props.location.state.role,
            isAdmin: this.props.location.state.isAdmin
        }
    }

    setInputValue(property, value) {
        value = value.trim();
        if (value.length > 12) {
            return;
        }
        this.setState({
            [property]: value
        })
    }

    handleDropdownChange(event) {
        this.setState({
            role: event.target.value
        })
    }

    replacer(key, value) {
        if (value === null || value ==='')
            return undefined;
        else
            return value;
    }

    async editUser() {
        let url, body;
        if (localStorage.getItem("role") === 'ADMIN') {
            url = process.env.REACT_APP_API_URL + '/users/update';
            body = JSON.stringify({
                id: this.props.location.state.id,
                username: this.state.username,
                password: this.state.password,
                email: this.state.email,
                role: this.state.role
            }, this.replacer);
        } else {
            url = process.env.REACT_APP_API_URL + '/users/me/update';
            body = JSON.stringify({
                username: this.state.username,
                password: this.state.password,
                email: this.state.email
            }, this.replacer);
        }
        try {
            let response = await fetch(url, {
                method: 'post',
                headers: new Headers({
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    "Authorization": 'Bearer ' + localStorage.getItem("jwtToken")
                }),
                body: body
            });

            let result = await response.json();
            if(result){
                console.log(result);
                this.props.history.push(this.props.location.state.origin)
            }
        }
        catch(e) {
            console.log(e)
        }
    }

    async deleteUser() {
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/users/delete?id=' + this.props.location.state.id, {
                method: 'post',
                headers: new Headers({
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    "Authorization": 'Bearer ' + localStorage.getItem("jwtToken")

                })
            });

            let result = await response.json();
            if(result){
                console.log(result);
                this.props.history.push('/users')
            }
        }
        catch(e) {
            console.log(e)
        }
    }

    render() {
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
            if(this.state.isAdmin) {
                return (
                    <div className="container">
                    <NavBar/>
                    <div className='editUserContainer'>
                        <PreviousPageIcon
                            where="/users"
                            hist={this.props.history}
                        />
                        <h1>Edit User</h1>
                        <InputField
                            type='text'
                            value={this.state.username}
                            onChange= { (value) => this.setInputValue('username', value)}
                            label ='Username'
                        />
                        <InputField
                            type='password'
                            placeholder='Enter new password'
                            value={this.state.password ? this.state.password : ''}
                            onChange= { (value) => this.setInputValue('password', value)}
                            label ='Password'
                        />
                        <InputField
                            type='text'
                            value={this.state.email}
                            onChange= { (value) => this.setInputValue('email', value)}
                            label ='Email'
                        />
                        <div className="form-group">
                            <label className='col-form-label'>Role</label>
                            <select 
                                className="form-control"
                                value={this.state.role}
                                onChange={this.handleDropdownChange.bind(this)}>
                                <option value="ADMIN">Admin</option>
                                <option value="RESEARCHER">Researcher</option>
                                <option value="GUEST">Guest</option>
                            </select>
                        </div>
                        <div className="btn-toolbar justify-content-between" role="toolbar">
                            <SubmitButton
                                text='Edit User'
                                type='btn-outline-secondary btn-lg'
                                onClick={ () => this.editUser() }                        
                            />
    
                            <SubmitButton
                                text='Delete User'
                                type='btn-outline-danger btn-lg'
                                onClick={ () => {if (window.confirm('Are you sure you want to delete this user?')) this.deleteUser()} }                        
                            />
                        </div>
    
                    </div>
                    </div>
                );
            }
            else {
                return (
                    <div className="container">
                    <NavBar/>
                    <div className='editUserContainer'>
                        <PreviousPageIcon
                            where="/users"
                            hist={this.props.history}
                        />
                        <h1>Edit User</h1>
                        <InputField
                            type='text'
                            value={this.state.username}
                            onChange= { (value) => this.setInputValue('username', value)}
                            label ='Username'
                        />
                        <InputField
                            type='text'
                            placeholder='Enter new password'
                            value={this.state.password ? this.state.password : ''}
                            onChange= { (value) => this.setInputValue('password', value)}
                            label ='Password'
                        />
                        <InputField
                            type='text'
                            value={this.state.email}
                            onChange= { (value) => this.setInputValue('email', value)}
                            label ='Email'
                        />
                        <div className="btn-toolbar justify-content-between" role="toolbar">
                            <SubmitButton
                                text='Edit User'
                                type='btn-outline-secondary btn-lg'
                                onClick={ () => this.editUser() }                        
                            />
                        </div>
                    </div>
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

export default EditUser;