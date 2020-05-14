import React from 'react';
import NavBar from '../../util/NavBar';
import { Redirect } from 'react-router-dom';
import SubmitButton from '../../util/SubmitButton';
import {checkJwtToken} from '../../util/Common';
import Moment from 'moment';
import Modal from 'react-bootstrap/Modal';
import Cookie from "js-cookie";
import EllipsisText from "react-ellipsis-text";

class Alignments extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            isLoggedIn: true,
            items: [],
            show: false,
            showError: false,
            errormessage: null
        }
        this.viewAlignment = this.viewAlignment.bind(this);
    }

    async componentDidMount(){
        this.setState({isLoggedIn: await checkJwtToken()});
        this.getAlignments();
    }


    async getAlignments() {
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/align/list', {
                method: 'get',
                headers: new Headers({
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    "Authorization": 'Bearer ' + Cookie.get("jwtToken")

                })
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
                    this.setState({items: result});
                }
            }
        }
        catch(e) {
            this.setState({errormessage: "Cannot connect to server, please try again later."})
            this.setState({showError:true});
            console.log("Cannot connect to server. " + e);
        }
    }

    handleClose = () => {this.setState({show: false})};
    handleShow = () => {this.setState({show: true})};

    addModal() {
        return (
          <>
            <Modal 
                show={this.state.show} 
                onHide={this.handleClose}
                size="lg"
                aria-labelledby="contained-modal-title-vcenter"
                centered>
              <Modal.Header closeButton>
                <Modal.Title id="contained-modal-title-vcenter">Choose an aligner</Modal.Title>
              </Modal.Header>
              <Modal.Body>
                  <div className="container">
                    <div className="row d-flex justify-content-around">
                        <div className="p-2">
                            <button type="button" className="btn btn-light btn-sq" style={{backgroundColor: "#e3f2fd"}}
                                onClick={() => this.props.history.push("/alignments/add", {aligner: "Bowtie"})}>
                                Bowtie
                            </button>
                        </div>
                        <div className="p-2">
                            <button type="button" className="btn btn-light btn-sq" style={{backgroundColor: "#e3f2fd"}}
                                onClick={() => this.props.history.push("/alignments/add", {aligner: "Bwa"})}>
                                BWA
                            </button>
                        </div>
                        <div className="p-2">
                            <button type="button" className="btn btn-light btn-sq" style={{backgroundColor: "#e3f2fd"}}
                                onClick={() => this.props.history.push("/alignments/add", {aligner: "Snap"})}>
                                Snap
                            </button>
                        </div>
                    </div>
                  </div>
                </Modal.Body>
                <Modal.Footer>
                    <SubmitButton type=" btn-secondary btn-lg" text="Close" onClick={this.handleClose}/>
                </Modal.Footer>
            </Modal>
          </>
        );
      }

    viewAlignment(event, item) {
        this.props.history.push("/alignments/igv", {item: item});
    }

    getBaseHtml() {
        return (
            <div className="container table-responsive-lg">
                <h1>
                    Alignments
                </h1>
                {this.addModal()}
                <br/>
                <table className="table table-hover">
                    <thead style={{backgroundColor: "#e3f2fd"}}>
                        <tr>
                            <th scope="col">Name</th>
                            <th scope="col">Aligner</th>
                            <th scope="col">Description</th>
                            <th scope="col">Owner</th>
                            <th scope="col">Visibility</th>
                            <th scope="col">Updated By</th>
                            <th scope="col">Updated At</th>
                            <th scope="col">Created At</th>
                        </tr>
                    </thead>
                    <tbody>
                    {this.state.items.map(function(item, index) {
                        return <tr 
                                key={index} 
                                onClick={(e) =>this.viewAlignment(e, item)}
                                data-toogle="tooltip" data-placement="top" title="Click to view alignment">
                                <th>{item.name}</th>
                                <td> {item.aligner} </td>
                                <td><EllipsisText text={item.description} length={100} /></td>
                                <td> {item.owner} </td>
                                <td> {item.visibility} </td>
                                <td> {item.updatedBy} </td>
                                <td> {item.updatedAt ? Moment(item.updatedAt).format("YYYY.MM.DD. HH:mm") : ""} </td>
                                <td> {item.createdAt ? Moment(item.createdAt).format("YYYY.MM.DD. HH:mm") : ""} </td>
                            </tr>
                    }, this)}
                    </tbody>
                </table>
            </div>);
    }

    render() {
        Moment.locale('en');
        let role = localStorage.getItem("role");
        if(this.state.isLoggedIn) {
            return(
                <div className="container">
                    <NavBar active="alignments"/>
                    {this.getBaseHtml()}
                    <br/>
                    {role === "ADMIN" || role === "RESEARCHER" ?
                    <SubmitButton
                            text='Create alignment'
                            type='btn-lg btn-outline-secondary'
                            onClick={ () => this.handleShow()}
                        /> : null }

                    {this.state.showError ? <div className="alert alert-primary mt-3" role="alert">{this.state.errormessage}</div> : null }
                </div>);

        }
        else { 
            return(
                <Redirect to="/login" />
            );
        }
    }
}

export default Alignments;