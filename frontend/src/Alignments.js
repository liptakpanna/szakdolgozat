import React from 'react';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import SubmitButton from './SubmitButton';
import InputField from './InputField';
import {checkJwtToken} from './Common';

class Alignments extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            referenceFile: null,
            readFile: null,
            name: '',
            description: '',
            visibility: '',
            aligner: 'BOWTIE',
        }
        this.handleDropdownChange = this.handleDropdownChange.bind(this);
    }

    componentDidMount() {
        checkJwtToken();
    }

    onChangeHandler(event, file){
        this.setState({
            [file]: event.target.files[0],
            loaded: 0,
          });
    }

    onClickHandler(event){
        let data = new FormData();
        data.append('referenceDna', this.state.referenceFile);
        data.append('readsForDna', this.state.readFile);
        data.append("name", this.state.name);
        data.append("description", this.state.description);
        data.append("aligner", this.state.aligner);
        data.append("visibility", this.state.visibility);
        data.append("usernameAccessList", null);
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


    render() {
        if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
            return(
                <div className="container">
                    <NavBar/>
                    <div className="container">
                        <h1> Create new alignment </h1>
                        <InputField
                            type='text'
                            value={this.state.name}
                            onChange= { (value) => this.setState({name: value})}
                            label ='Name'
                        />
                        <InputField
                            type='textarea'
                            value={this.state.description}
                            onChange= { (value) => this.setState({description: value})}
                            label ='Description'
                        />
                        <div className="form-group">
                            <label className='col-form-label'>Aligner</label>
                            <select 
                                className="form-control"
                                value={this.state.aligner}
                                onChange={(e) => this.handleDropdownChange(e, 'aligner')}>
                                <option value="BOWTIE">Bowtie</option>
                            </select>
                        </div>
                        <div className="form-group">
                            <label className='col-form-label'>Reference DNA file</label>
                            <br/>
                            <input className="form-control-title" type="file" onChange={ (e) => this.onChangeHandler(e, "referenceFile")}/>
                        </div>
                        <div className="form-group">
                            <label className='col-form-label'>Reads file</label>
                            <br/>
                            <input className="form-control-title" type="file" onChange={ (e) => this.onChangeHandler(e, "readFile")}/>
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

export default Alignments;