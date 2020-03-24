import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import InputField from './InputField';
import SubmitButton from './SubmitButton';
import {checkJwtToken} from './Common';
import Modal from 'react-bootstrap/Modal';
import PreviousPageIcon from './PreviousPageIcon';
import _ from 'lodash';

class EditUser extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            item: this.props.location.state.item,
            isAdmin: this.props.location.state.isAdmin,
            show: false,
            showError: false,
            errormessage: "There are no modifications"
        }
    }

    componentDidMount(){
        checkJwtToken();
    }

    setInputValue(property, value) {
        value = value.trim();
        if(this.state.showError) {
            this.setState({showError: false})
        }
        this.setState({ item: { ...this.state.item, [property]: value} });
    }

    handleDropdownChange(event) {
        if(this.state.showError) {
            this.setState({showError: false})
        }
        this.setState({ item: { ...this.state.item, role: event.target.value} });
    }

    replacer(key, value) {
        if (value === null || value ==='')
            return undefined;
        else
            return value;
    }

    isChanged(){
        return !_.isEqual(this.state.item,this.props.location.state.item);
    }

    async editUser() {
        if(this.isChanged()) {
            let url, body;
            if (this.state.isAdmin) {
                url = process.env.REACT_APP_API_URL + '/users/update';
                body = JSON.stringify({
                    id: this.props.location.state.item.id,
                    username: this.state.item.username,
                    password: this.state.item.password,
                    email: this.state.item.email,
                    role: this.state.item.role
                }, this.replacer);
            } else {
                url = process.env.REACT_APP_API_URL + '/users/me/update';
                body = JSON.stringify({
                    username: this.state.item.username,
                    password: this.state.item.password,
                    email: this.state.item.email
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
                    if(result.status === 500) {
                        this.setState({errormessage: "Username already in use"})   
                        this.setState({showError:true});
                    } else {
                        console.log(result);
                        this.props.history.push(this.props.location.state.origin)
                    }
                }
            }
            catch(e) {
                console.log(e)
            }
        } else{
            this.setState({showError: true});
            this.setState({errormessage: "There are no modifications."})   
        } 
    }

    async deleteUser() {
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/users/delete?id=' + this.props.location.state.item.id, {
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

    addRoleDropdown(){
        if(this.state.isAdmin){
            return(
                <div className="form-group">
                            <label className='col-form-label'>Role</label>
                            <select 
                                className="form-control"
                                value={this.state.item.role}
                                onChange={this.handleDropdownChange.bind(this)}>
                                <option value="ADMIN">Admin</option>
                                <option value="RESEARCHER">Researcher</option>
                                <option value="GUEST">Guest</option>
                            </select>
                </div>
            )
        }
    }
  
    handleClose = () => {this.setState({show: false})};
    handleShow = () => {this.setState({show: true})};

    addModal(){
        return(
            <Modal show={this.state.show} onHide={this.handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Are you sure you want to delete this user?</Modal.Title>
                </Modal.Header>
                <Modal.Footer>
                    <SubmitButton type=" btn-secondary" text="Close" onClick={this.handleClose}/>
                    <SubmitButton type=" btn-danger" text="Delete" onClick={() => this.deleteUser()}/>
                </Modal.Footer>
            </Modal>
        )
    }

    addDeleteButton(){
        if(this.state.isAdmin){
            return(
                <SubmitButton
                text='Delete User'
                type='btn-outline-danger btn-lg'
                onClick={ () => {this.handleShow()} }                        
                />
            )
        }
    }

    render() {
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
            return (
                <div className="container">
                <NavBar/>
                <div className='editUserContainer'>
                    <PreviousPageIcon
                        where={this.props.location.state.origin}
                        hist={this.props.history}
                    />
                    <h1>Edit User</h1>
                    {this.addModal()}
                    <InputField
                        type='text'
                        value={this.state.item.username}
                        onChange= { (value) => this.setInputValue('username', value)}
                        label ='Username'
                    />
                    <InputField
                        type='password'
                        placeholder='Enter new password'
                        value={this.state.item.password ? this.state.item.password : ''}
                        onChange= { (value) => this.setInputValue('password', value)}
                        label ='Password'
                    />
                    <InputField
                        type='text'
                        value={this.state.item.email}
                        onChange= { (value) => this.setInputValue('email', value)}
                        label ='Email'
                    />
                    {this.addRoleDropdown()}
                    <div className="btn-toolbar justify-content-between" role="toolbar">
                        <SubmitButton
                            text='Edit User'
                            type='btn-outline-secondary btn-lg'
                            onClick={ () => this.editUser() }                        
                        />
                        {this.addDeleteButton()}
                    </div>
                    { this.state.showError ? <div className="alert alert-primary mt-3" role="alert">{this.state.errormessage}</div> : null }
                </div>
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

export default EditUser;