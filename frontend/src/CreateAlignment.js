import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import SubmitButton from './SubmitButton';
import InputField from './InputField';
import {checkJwtToken} from './Common';
import PreviousPageIcon from './PreviousPageIcon';
import { Multiselect } from 'react-widgets';

class CreateAlignment extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            referenceFile: null,
            readFile: [],
            name: '',
            description: '',
            visibility: 'PUBLIC',
            referencId: null,
            refType: "example",
            references: [],
            userAccess: [],
            usernames: []
        }
        this.handleDropdownChange = this.handleDropdownChange.bind(this);
    }

    componentDidMount() {
        checkJwtToken();
        this.getReferences();
        this.getUsernames();
    }

    onChangeHandler(event, file){
        this.setState({
            [file]: event.target.files,
            loaded: 0,
          });
    }

    onClickHandler(event){
        let data = new FormData();
        if(this.state.referenceFile)
            data.append('referenceDna', this.state.referenceFile);
        for (var x = 0; x < this.state.readFile.length; x++) {
            data.append("readsForDna", this.state.readFile[x]);
        }
        data.append("name", this.state.name);
        data.append("description", this.state.description);
        data.append("aligner", this.props.location.state.aligner.toUpperCase());
        data.append("visibility", this.state.visibility);
        data.append("usernameAccessList", this.state.userAccess);
        data.append("referenceId", this.state.referencId);
        this.upload(data);
    }

    async upload(data){
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/align', {
                method: 'post',
                headers: new Headers({
                    'Accept': 'application/json',
                    "Authorization": 'Bearer ' + localStorage.getItem("jwtToken")
                }),
                body: data
            });

            let result = await response.json();
            if(result){
                console.log(result);
                this.props.history.push("/alignments/igv", {item : result});
            }
        }
        catch(e) {
            console.log(e)
        }
    };

    handleDropdownChange(event, dropdown) {
        this.setState({
            [dropdown]: event.target.value
        })
    }

    onRadioChange(event) {
        this.setState({
          refType: event.currentTarget.value,
          referencId: null
          });
    }

    showReferenceUpload(){
        return(
            <div className="form-group">
                <label className='col-form-label'>Reference DNA file</label>
                <br/>
                <input className="form-control-title" type="file" onChange={ (e) => this.onChangeHandler(e, "referenceFile")}/>
            </div>
        );
    }

    async getReferences(){
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/align/referencelist', {
                method: 'get',
                headers: new Headers({
                    'Accept': 'application/json',
                    "Authorization": 'Bearer ' + localStorage.getItem("jwtToken")
                })
            });

            let result = await response.json();
            if(result){
                console.log(result);
                this.setState({references: result});
            }
        }
        catch(e) {
            console.log(e)
        }
    }

    showReferenceExample(){
        let btnclass = "btn btn-light btn-sq ";
        return(
            <div className="container">
                <div className="row d-flex justify-content-around">
                    {this.state.references.map(function(item, index) {
                            return <div className="p-2" key={index}>
                                <button type="button" 
                                className={this.state.referencId === item.id ? btnclass+'active' : btnclass}
                                onClick={() => this.setState({referencId: item.id})}
                                data-toogle="tooltip" data-placement="top" title={item.description}>
                                {item.name}
                                </button>
                            </div>
                    }, this)}
                </div>
            </div>
        );
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

    addUserAccessList(){
        if(this.state.visibility === "PRIVATE" || this.state.visibility === "TOPSECRET") {
            if(this.state.usernames && this.state.usernames.length > 0) {
                return(
                    <div>
                        <label htmlFor="userSelect">View permission for users: </label>
                            <Multiselect
                                data={this.state.usernames}
                                value={this.state.userAccess}
                                onChange={value => {this.setState({ userAccess: value })}}
                            />
                    </div>
                )
            }
        }
    }

    render() {
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
            return(
                <div className="container">
                    <NavBar/>
                    <div className="container">
                        <PreviousPageIcon
                            where="/alignments"
                            hist={this.props.history}
                        />
                        <h1> Create new alignment with {this.props.location.state.aligner}</h1>
                        <InputField
                            type='text'
                            value={this.state.name}
                            onChange= { (value) => this.setState({name: value})}
                            label ='Name'
                            maxLength="20"
                        />
                        <div className="form-group">
                            <label >Description</label>
                            <textarea 
                                value={this.state.description} 
                                className="form-control" 
                                rows="4" maxLength='1000' 
                                onChange= { (event) => this.setState({description: event.target.value})}>
                            </textarea>
                        </div>
                        <div className="form-check form-check-inline">
                            <input className="form-check-input" type="radio" name="inlineRadioOptions" value="example" id="exampleRef"
                                checked={this.state.refType === "example"}
                                onChange={(e) => this.onRadioChange(e)}/>
                            <label className="form-check-label" htmlFor="exampleRef">Choose reference dna</label>
                        </div>
                        <div className="form-check form-check-inline">
                            <input className="form-check-input" type="radio" name="inlineRadioOptions" value="upload" id="uploadRef"
                            checked={this.state.refType === "upload"}
                            onChange={(e) => this.onRadioChange(e)}/>
                            <label className="form-check-label" htmlFor="uploadRef">Upload reference dna</label>
                        </div>
                        {this.state.refType === "upload" ? this.showReferenceUpload() : this.showReferenceExample()}
                        <div className="form-group">
                            <label className='col-form-label'>Reads file</label>
                            <br/>
                            <input className="form-control-title" type="file" multiple onChange={ (e) => this.onChangeHandler(e, "readFile")}/>
                        </div>

                        <div className="form-group">
                            <label className='col-form-label'>Visibility</label>
                            <select 
                                className="form-control"
                                value={this.state.visibility}
                                onChange={(e) => this.handleDropdownChange(e,'visibility')}>
                                <option value="PUBLIC">Public</option>
                                <option value="PRIVATE">Private</option>
                                <option value="TOPSECRET">TopSecret</option>
                            </select>
                        </div>
                        {this.addUserAccessList()}
                    </div>
                    <br/>
                    <SubmitButton
                            text='CREATE'
                            type='btn-outline-secondary btn-lg'
                            onClick={ (e) => this.onClickHandler(e)}
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

export default CreateAlignment;