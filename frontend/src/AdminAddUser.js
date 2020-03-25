import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import InputField from './InputField';
import SubmitButton from './SubmitButton';
import PreviousPageIcon from './PreviousPageIcon';
import Cookie from "js-cookie";
import {validateEmail} from './Common';

class AdminAddUser extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            email: '',
            role: 'GUEST',
            showError: false,
            errormessage: null
        }
    }

    setInputValue(property, value) {
        value = value.trim();
        this.setState({
            [property]: value
        })
    }

    handleDropdownChange(event) {
        this.setState({
            role: event.target.value
        })
    }

    async addUser() {
        if (!this.state.username || !this.state.password || !this.state.email) {return;}
        if(!validateEmail(this.state.item.email)) {
            this.setState({errormessage: "Not a valid email form"})   
            this.setState({showError:true});
            return;
        }
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/users/add', {
                method: 'post',
                headers: new Headers({
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    "Authorization": 'Bearer ' + Cookie.get("jwtToken")

                }),
                body:JSON.stringify({
                    username: this.state.username,
                    password: this.state.password,
                    email: this.state.email,
                    role: this.state.role
                })
            }).catch(error =>  {
                this.setState({errormessage: "Cannot connect to server"})   
                this.setState({showError:true});
                console.log("Cannot connect to server");
            });

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
                    console.log(result)
                    this.props.history.push('/users')    
                }
            }
        }
        catch(e) {
            console.log("megint mas: ");
            console.log(e)
        }
    }

    render() {
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
            return (
                <div className="container">
                <NavBar/>
                <div className='newUserContainer'>
                    <PreviousPageIcon
                        where="/users"
                        hist={this.props.history}
                    />
                    <form onSubmit={(e) => e.preventDefault()}>
                    <h1>Add New User</h1>
                    <InputField
                        type='text'
                        placeholder='Enter username'
                        value={this.state.username ? this.state.username : ''}
                        onChange= { (value) => this.setInputValue('username', value)}
                        label ='Username'
                        required={true}
                    />
                    <InputField
                        type='password'
                        placeholder='Enter password'
                        value={this.state.password ? this.state.password : ''}
                        onChange= { (value) => this.setInputValue('password', value)}
                        label ='Password'
                        required={true}
                    />
                    <InputField
                        type='email'
                        placeholder='Enter email'
                        value={this.state.email ? this.state.email : ''}
                        onChange= { (value) => this.setInputValue('email', value)}
                        label ='Email'
                        maxLength="50"
                        required={true}
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

                    <SubmitButton
                        text='Add User'
                        type='btn-outline-secondary'
                        onClick={ () => this.addUser() }                        
                    />  
                    </form>
                </div>
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

export default AdminAddUser;