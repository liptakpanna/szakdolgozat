import React from 'react';
import NavBar from '../../util/NavBar';
import { Redirect } from 'react-router-dom';
import SubmitButton from '../../util/SubmitButton';
import InputField from '../../util/InputField';
import {checkJwtToken} from '../../util/Common';
import PreviousPageIcon from '../../util/PreviousPageIcon';
import { Multiselect } from 'react-widgets';
import Cookie from "js-cookie";
import EllipsisText from "react-ellipsis-text";

class CreateAlignment extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            isLoggedIn: true,
            referenceFile: null,
            readFile: [
                {
                    name: null,
                    file: [],
                    radio: "specific",
                    specific: 1,
                    mismatch: 2,
                    penalty: [4,6,1],
                    maxHits: 300,
                    maxDist: 8
                }
            ],
            name: '',
            description: '',
            visibility: 'PUBLIC',
            referencId: null,
            refType: "example",
            references: [],
            userAccess: [],
            usernames: [],
            trackCount: 1,
            aligner: this.props.location.state.aligner ? this.props.location.state.aligner : "Bowtie",
            showError: false,
            errormessage: null,
            isLoading: false,
            acceptedFormat: this.props.location.state.aligner === "Snap" ? ".fastq,.fq"  : ".fastq,.fq,.fasta,.fna,.fa" ,
        }
        this.handleDropdownChange = this.handleDropdownChange.bind(this);
        this.regexp = /^[A-Za-z0-9\s]*$/;
    }

    async componentDidMount() {
        this.setState({isLoggedIn: await checkJwtToken()});
        this.getReferences();
        this.getUsernames();
    }

    onChangeHandler(event, file){
        this.setState({
            [file]: event.target.files[0],
            loaded: 0,
        });
    }

    onClickHandler(event){
        if(!this.state.name || this.state.readFile.length === 1 ) {return;}
        let data = new FormData();
        if(!this.state.referenceFile && this.state.refType ==="upload") {return;}
        else if(this.state.refType !=="upload" && !this.state.referencId) {
            this.setState({showError: true});
            this.setState({errormessage: "Please choose reference genome or upload one."})
            return;
        }
        if(!this.regexp.test(this.state.name)) {
            this.setState({showError:true});
            this.setState({errormessage:"The alignment name can only contain letters, numbers and space."});
            return;
        }
        if(this.state.referenceFile)
            data.append('referenceDna', this.state.referenceFile);
        for (let x = 0; x < this.state.readFile.length-1; x++) {
            if(!this.state.readFile[x].name || !this.state.readFile[x].file[0]) {return;}
            let isPaired = this.state.readFile[x].file.length === 2;
            data.append("readsForDna[" + x + "].name", this.state.readFile[x].name);
            data.append("readsForDna[" + x + "].isPaired", isPaired);
            data.append("readsForDna[" + x + "].read1", this.state.readFile[x].file[0]);
            if(isPaired)
                data.append("readsForDna[" + x + "].read2", this.state.readFile[x].file[1]);
            if(this.state.aligner === "Bowtie") {
                data.append("readsForDna[" + x + "].validCount", this.state.readFile[x].radio !== "specific" ? this.state.readFile[x].radio : this.state.readFile[x].specific );
                data.append("readsForDna[" + x + "].mismatch", this.state.readFile[x].mismatch);
            }
            else if(this.state.aligner ==="Bwa")
                data.append("readsForDna[" + x + "].penalties", this.state.readFile[x].penalty);
            else if(this.state.aligner ==="Snap") {
                data.append("readsForDna[" + x + "].maxDist", this.state.readFile[x].maxDist);
                data.append("readsForDna[" + x + "].maxHits", this.state.readFile[x].maxHits);
            }
        }
        data.append("name", this.state.name);
        data.append("description", this.state.description);
        data.append("aligner", this.state.aligner.toUpperCase());
        data.append("visibility", this.state.visibility);
        data.append("usernameAccessList", this.state.userAccess);
        if(this.state.referencId != null)
            data.append("referenceId", this.state.referencId);
        this.setState({isLoading: true});
        this.upload(data);
    }

    async upload(data){
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/align', {
                method: 'post',
                headers: new Headers({
                    'Accept': 'application/json',
                    "Authorization": 'Bearer ' + Cookie.get("jwtToken")
                }),
                body: data
            }).catch(error =>  {
                this.setState({errormessage: "Cannot connect to server, please try again later."})   
                this.setState({showError:true});
                this.setState({isLoading: false});
                console.log("Cannot connect to server");
            })

            let result = await response.json();
            if(result){
                this.setState({isLoading: false});
                if(response.status === 500) {
                    if(result.message.includes("Maximum upload size exceeded")){
                        let max = result.message.substring(result.message.lastIndexOf("(")+1, result.message.length-1);
                        this.setState({errormessage: "Maximum upload size (" + max/1000000 + "MB) exceeded."})
                    }
                    else if(result.message === "Wrong file type")
                        this.setState({errormessage: result.message + ", please upload reference genome in FASTA format and read files with one of the following extensions: " + this.state.acceptedFormat})
                    else
                        this.setState({errormessage: result.message})   
                    this.setState({showError:true});
                }
                else if(response.status === 403) {
                    this.props.history.push("/login");
                }
                else{
                    console.log(result);
                    this.props.history.push("/alignments/igv", {item : result});
                }
            }
        }
        catch(e) {
            this.setState({isLoading: false});
            this.setState({errormessage: "Cannot connect to server, please try again later."})   
            this.setState({showError:true});
            this.setState({isLoading: false});
            console.log("Cannot connect to server. " + e);
        }
    };

    handleDropdownChange(event) {
        if(event.target.value === "PUBLIC") this.setState({userAccess : []});
        this.setState({
            visibility: event.target.value
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
                <h4 className="mt-1 mb-0">Reference DNA file</h4>
                <br/>
                <input className="form-control-title" id="referenceFile" style={{"display":"none"}} type="file" accept=".fasta,.fna,.fa"  required onChange={ (e) => this.onChangeHandler(e, "referenceFile")}/>
                <label className="pointer" htmlFor="referenceFile">
                    <span className="fileInput mr-2">Choose file</span>
                    {console.log(this.state.referenceFile)}
                    {this.state.referenceFile === null ? " No file chosen" : this.state.referenceFile.name}
                </label>
            </div>
        );
    }

    async getReferences(){
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/align/referencelist', {
                method: 'get',
                headers: new Headers({
                    'Accept': 'application/json',
                    "Authorization": 'Bearer ' + Cookie.get("jwtToken")
                })
            })

            let result = await response.json();
            if(result){
                if(response.status === 500) {
                    this.setState({errormessage: result.message})   
                    this.setState({showError:true});
                }
                else if(result.status === 403) {
                    this.props.history.push("/login");
                }
                else{
                    console.log(result);
                    this.setState({references: result});
                } 
            }
        }
        catch(e) {
            this.setState({errormessage: "Cannot connect to server, please try again later."})   
            this.setState({showError:true});
            console.log("Cannot connect to server. " + e);
        }
    }

    showReferenceExample(){
        let btnclass = "btn btn-light ";
        return(
            <div className="container">
                <div className="row d-flex justify-content-around">
                    {this.state.references.map(function(item, index) {
                            return <div className="p-2" key={index}>
                                <button type="button" style={{backgroundColor: "#e3f2fd"}}
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
                    "Authorization": 'Bearer ' + Cookie.get("jwtToken")
                })
            });

            let result = await response.json();
            if(result){
                if(response.status === 500) {
                    this.setState({errormessage: result.message})   
                    this.setState({showError:true});
                }
                else if(result.status === 403) {
                    this.props.history.push("/login");
                }
                else{
                    console.log(result);
                    this.setState({usernames: result});
                } 
            }
        }
        catch(e) {
            this.setState({errormessage: "Cannot connect to server, please try again later."})   
            this.setState({showError:true});
            console.log("Cannot connect to server. " + e);
        }
    }

    addUserAccessList(){
        if(this.state.visibility === "PRIVATE" || this.state.visibility === "PRIVATE_GROUP") {
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

    setValueForRead(property, value, index) {
        let reads = [...this.state.readFile];
        let read = {...reads[index]};
        if(property === "name")
            read.name = value;
        else if (property === "file") {
            if(value.length > 2) {
                this.setState({errormessage: "Maximum two files per track"})   
                this.setState({showError:true});
                return;
            }
            else { 
                this.setState({showError:false});
                read.file = value;
            } 
        }
        else if (property === "radio") {
            if(value === " --best")
                read.radio += value;
            else
                read.radio = value;
        }
        else if (property === "specific")
            read.specific = value;
        else if (property === "mismatch")
            read.mismatch = value;
        else if(property === "penaltyMis")
            read.penalty[0] = value;
        else if(property === "penaltyGapOpen")
            read.penalty[1] = value;
        else if(property === "penaltyGapExt")
            read.penalty[2] = value;
        else if (property === "maxdist")
            read.maxDist = value;
        else if(property === "maxhits")
            read.maxHits = value;
        reads[index] = read;
        if(read.name === "" && read.file.length === 0 && index+1 < reads.length) {
            reads.splice(index, 1);
            console.log(reads);
            this.setState({trackCount: this.state.trackCount-1});
        }
        if(read.name !== null && read.file.length > 0 && index+1 === reads.length) {
            this.setState({trackCount: this.state.trackCount+1});
            let newRead = {...reads[index+1]};
            newRead.name = null;
            newRead.file = [];
            newRead.specific = 1;
            newRead.radio = "specific";
            newRead.mismatch = 2;
            newRead.penalty = [4,6,1];
            newRead.maxDist = 8;
            newRead.maxHits = 300;
            reads[index+1] = newRead;
        }
        this.setState({readFile: reads});
    }

    addOptions(i){
        if(this.state.aligner === "Bowtie") {
            return(<>
                <td> 
                <div className="form-check-inline">
                    <input className="form-check-input" type="radio" name={"inlineRadioOptions"+i} 
                        checked={this.state.readFile[i].radio !== "specific"} 
                        value="all"
                        onChange={(e) => this.setValueForRead("radio", e.currentTarget.value, i)}/>
                    <label className="form-check-label mr-2">all</label> 
                    <input type="checkbox" className="form-check-input" value=" --best" name={"inlineCheckBox"+i}
                        onChange={(e) => this.setValueForRead("radio", e.currentTarget.value, i)}
                        disabled={this.state.readFile[i].radio === "specific"}
                        />
                    <label className="form-check-label">best-to-worst</label>
                </div>
                <div className="form-check-inline">
                    <input className="form-check-input" type="radio" name={"inlineRadioOptions"+i} 
                        checked={this.state.readFile[i].radio === "specific"} 
                        value="specific"
                        onChange={(e) => this.setValueForRead("radio", e.currentTarget.value, i)}/>
                    <label className="form-check-label mr-2">number: </label>
                    <input type="number" min="1" length="2" style={{"width":"50px"}}
                        value={this.state.readFile[i].specific} 
                        onChange= { (e) => this.setValueForRead("specific", e.target.value, i)}
                        disabled={this.state.readFile[i].radio !== "specific"}
                        /> 
                </div>
                </td>
                <td>
                    <input type="number" min="0" max="3" length="2" style={{"width":"50px"}}
                        value={this.state.readFile[i].mismatch} 
                        onChange= { (e) => this.setValueForRead("mismatch", e.target.value, i)}
                    /> 
                </td>
                </>
            )
        }
        else if(this.state.aligner === "Bwa") {
            return(<>
                <td data-toogle="tooltip" data-placement="top" title="Note: if your reads are <70bp the default values are: 3, 11, 4">
                    <input type="number" min="0" length="2" style={{"width":"50px"}}
                        value={this.state.readFile[i].penalty[0]} 
                        onChange= { (e) => this.setValueForRead("penaltyMis", e.target.value, i)}
                    /> 
                </td>
                <td data-toogle="tooltip" data-placement="top" title="Note: if your reads are <70bp the default values are: 3, 11, 4">
                    <input type="number" min="0" length="2" style={{"width":"50px"}}
                        value={this.state.readFile[i].penalty[1]} 
                        onChange= { (e) => this.setValueForRead("penaltyGapOpen", e.target.value, i)}
                    /> 
                </td>
                <td data-toogle="tooltip" data-placement="top" title="Note: if your reads are <70bp the default values are: 3, 11, 4">
                    <input type="number" min="0" length="2" style={{"width":"50px"}}
                        value={this.state.readFile[i].penalty[2]} 
                        onChange= { (e) => this.setValueForRead("penaltyGapExt", e.target.value, i)}
                    /> 
                </td>
            </>);
        }
        else if(this.state.aligner === "Snap") {
            return(<>
                <td data-toogle="tooltip" data-placement="top" title="Note: for paired-end reads the default is: 16000">
                    <input type="number" min="10" max="30000" length="5" style={{"width":"50px"}}
                        value={this.state.readFile[i].maxHits} 
                        onChange= { (e) => this.setValueForRead("maxhits", e.target.value, i)}
                    /> 
                </td>
                <td>
                    <input type="number" min="0" max="63" length="2" style={{"width":"50px"}}
                        value={this.state.readFile[i].maxDist} 
                        onChange= { (e) => this.setValueForRead("maxdist", e.target.value, i)}
                    /> 
                </td>
            </>);
        }
    }

    addOptionNames(){
        if(this.state.aligner==="Bowtie") {
            return(<>
            <th scope="col">Report valid alignments</th>
            <th scope="col">Most mismatches(0-3)</th>
            </>);
        }
        else if(this.state.aligner ==="Bwa") {
            return(<>
                <th scope="col">Mismatch penalty</th>
                <th scope="col">Gap open penalty</th>
                <th scope="col">Gap extension penalty</th>
                </>);
        }
        else if(this.state.aligner ==="Snap") {
            return(<>
                <th scope="col">Maximum Hits</th>
                <th scope="col">Maximum Distance</th>
                </>);
        }
    }

    addTrackInputs(){
        var tracks = [];
        for(let i=0; i < this.state.trackCount; i++) {
            tracks.push(<tr 
                key={i} >
                <th>{i+1}</th>
                <td className="fileInputWrapper"> <input id={"readfileInput" + i} className="form-control-title pr-0" type="file" accept={this.state.acceptedFormat}
                    required={this.state.trackCount === 1 || this.state.trackCount>i+1}
                    multiple onChange={ (e) => {this.setValueForRead("file", e.target.files, i)}}/>
                    <label htmlFor={"readfileInput" + i} className="pointer">
                        <span className="fileInput mr-2">Choose Files</span>
                        <EllipsisText length={15} text={this.state.readFile[i].file.length > 1 ? "2 files chosen" : (this.state.readFile[i].file.length === 0 ? "No file chosen" : this.state.readFile[i].file[0].name)}/>
                    </label>
                </td>
                <td> <input className="form-control"
                        type='text' style={{"width":"120px"}}
                        value={this.state.readFile[i].name ? this.state.readFile[i].name : ""}
                        onChange= { (e) => this.setValueForRead("name", e.target.value, i)}
                        placeholder ='Name'
                        maxLength="12"
                        required={this.state.trackCount === 1 || this.state.trackCount>i+1}
                        /> 
                </td>
                {this.addOptions(i)}
            </tr>);
        }
        return(<table className="table table-hover">
                <thead>
                    <tr>
                        <th scope="col">Track</th>
                        <th scope="col">Files</th>
                        <th scope="col">Name</th>
                        {this.addOptionNames()}
                    </tr>
                </thead>
                <tbody>
                {tracks}
                </tbody>
             </table>);
    }

    handleClose = () => {this.setState({isLoading: false})};

    addModal(){
        if(this.state.isLoading){
            return(
                    <div className="loading">Loading&#8230;</div>
            )
        }
    }

    nameChangeHandler(value){
        if(this.state.showError && (this.state.errormessage === "Alignment name already exists, please choose an other one." || this.state.errormessage === "The alignment name can only contain letters, numbers and space.")) {
            this.setState({showError:false});
        }
        this.setState({name: value});
    }

    render() {
        if(this.state.isLoggedIn) {
            return(
                <div className="container">
                    <NavBar active="alignments" disabled={this.state.isLoading}/>
                    <div className="container">
                        <PreviousPageIcon
                            where="/alignments"
                            hist={this.props.history}
                        />
                        <h1 className="d-inline"> Create new alignment with {this.state.aligner}</h1>
                        <form onSubmit={(e) => e.preventDefault()}>
                            <InputField
                                type='text'
                                value={this.state.name}
                                onChange= { (value) => this.nameChangeHandler(value)}
                                label ='Name'
                                maxLength="20"                        
                                required={true}
                            />
                            <div className="form-group">
                                <label >Description (optional)</label>
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
                            <h4>Read files</h4>
                            <p>Select one file for single end read, two for paired end reads</p>
                            <div className="container table-responsive-lg">
                                {this.addTrackInputs()}
                            </div>
                            <div className="form-group">
                                <label className='col-form-label'>Visibility</label>
                                <select 
                                    className="form-control"
                                    value={this.state.visibility}
                                    onChange={(e) => this.handleDropdownChange(e)}>
                                    <option value="PUBLIC">Public</option>
                                    <option value="PRIVATE">Private</option>
                                    <option value="PRIVATE_GROUP">Private Group</option>
                                </select>
                            </div>
                            {this.addUserAccessList()}
                            <br/>
                            <div className="d-flex justify-content-start">
                                <SubmitButton
                                        text='CREATE'
                                        type='btn-outline-secondary btn-lg'
                                        onClick={ (e) => this.onClickHandler(e)}
                                    />
                                {this.state.showError ? <div className="alert alert-primary ml-2" role="alert">{this.state.errormessage}</div> : null }
                            </div>
                        </form>
                    </div>  
                    {this.addModal()}
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

export default CreateAlignment;