import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import InputField from './InputField';
import SubmitButton from './SubmitButton';
import {checkJwtToken} from './Common';
import Modal from 'react-bootstrap/Modal';
import PreviousPageIcon from './PreviousPageIcon';
import _ from 'lodash';
import { Multiselect } from 'react-widgets';

class EditUser extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            item: this.props.location.state.item,
            show: false,
            showModified: false,
            username: []
        }
    }

    setInputValue(property, value) {
        if(this.state.showModified) {
            this.setState({showModified: false})
        }
        this.setState({ item: { ...this.state.item, [property]: value} });
    }

    handleDropdownChange(event) {
        if(this.state.showModified) {
            this.setState({showModified: false})
        }
        this.setState({ item: { ...this.state.item, visibility: event.target.value} });
    }

    replacer(key, value) {
        if (value === null || value ==='')
            return undefined;
        else
            return value;
    }

    componentDidMount(){
        checkJwtToken();
        this.getUsernames();
    }

    isChanged(){
        return !_.isEqual(this.state.item,this.props.location.state.item);
    }

    async editAlignment() {
        if(this.isChanged()){
            try {
                let response = await fetch(process.env.REACT_APP_API_URL + '/align/update', {
                    method: 'post',
                    headers: new Headers({
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                        "Authorization": 'Bearer ' + localStorage.getItem("jwtToken")
                    }),
                    body: JSON.stringify({
                        id: this.props.location.state.item.id,
                        name: this.state.item.name,
                        visibility: this.state.item.visibility,
                        description: this.state.item.description,
                        usernameAccessList: this.state.item.userAccess
                    }, this.replacer)
                });
    
                let result = await response.json();
                if(result){
                    console.log(result);
                    this.props.history.push('/alignments/igv', {item: result})
                }
            }
            catch(e) {
                console.log(e)
            }
        }else this.setState({showModified: true});
    }

    async deleteAlignment() {
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/align/delete?id=' + this.props.location.state.item.id, {
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
                this.props.history.push('/alignments')
            }
        }
        catch(e) {
            console.log(e)
        }
    }

    async getUsernames(){
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/users/usernamelist', {
                method: 'get',
                headers: new Headers({
                    'Accept': 'application/json',
                    "Authorization": 'Bearer ' + localStorage.getItem("jwtToken")
                })
            });

            let result = await response.json();
            if(result){
                console.log(result);
                this.setState({usernames: result.usernames});
            }
        }
        catch(e) {
            console.log(e)
        }
    }

    handleClose = () => {this.setState({show: false})};
    handleShow = () => {this.setState({show: true})};

    addModal(){
        return(
            <Modal show={this.state.show} onHide={this.handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Are you sure you want to delete this alignment?</Modal.Title>
                </Modal.Header>
                <Modal.Footer>
                    <SubmitButton type=" btn-secondary" text='Close' onClick={this.handleClose}/>
                    <SubmitButton type=" btn-danger" text="Delete" onClick={() => this.deleteAlignment()}/>
                </Modal.Footer>
            </Modal>
        )
    }

    addUserAccessList(){
        if(this.state.item.visibility === "PRIVATE" || this.state.item.visibility === "TOPSECRET") {
            if(this.state.usernames && this.state.usernames.length > 0) {
                return(
                    <div>
                        <label htmlFor="userSelect">View permission for users: </label>
                            <Multiselect
                                data={this.state.usernames}
                                value={this.state.item.userAccess}
                                onChange={value => {this.setState({ item: { ...this.state.item, userAccess : value} })}}
                            />
                    </div>
                )
            }
        }
    }

    render() {
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
                return (
                    <div className="container">
                    <NavBar/>
                    <div className='container'>
                        <PreviousPageIcon
                            where={'/alignments/igv'}
                            item = {this.state.item}
                            hist={this.props.history}
                        />
                        <h1>Edit Alignment</h1>
                        {this.addModal()}
                        <InputField
                            type='text'
                            value={this.state.item.name}
                            onChange= { (value) => this.setInputValue('name', value)}
                            label ='Name'
                        />
                        <div className="form-group">
                            <label >Description</label>
                            <textarea 
                                value={this.state.item.description} 
                                className="form-control" 
                                rows="4" maxLength='1000' 
                                onChange= { (event) => this.setInputValue('description', event.target.value)}>
                            </textarea>
                        </div>
                        <div className="form-group">
                            <label className='col-form-label'>Visibility</label>
                            <select 
                                className="form-control"
                                value={this.state.item.visibility}
                                onChange={this.handleDropdownChange.bind(this)}>
                                <option value="PUBLIC">Public</option>
                                <option value="PRIVATE">Private</option>
                                <option value="TOPSECRET">Top Secret</option>
                            </select>
                        </div>
                        {this.addUserAccessList()}
                        <div className="btn-toolbar justify-content-between" role="toolbar">
                            <SubmitButton
                                text='Edit Alignment'
                                type='btn-outline-secondary btn-lg'
                                onClick={ () => this.editAlignment() }                        
                            />
                            <SubmitButton
                                text='Delete Alignment'
                                type='btn-outline-danger btn-lg'
                                onClick={ () => {this.handleShow()} }                       
                            />
                        </div>
                        { this.state.showModified ? <div className="alert alert-primary mt-3" role="alert">There are no modifications</div> : null }
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