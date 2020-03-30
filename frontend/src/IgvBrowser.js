import React, { Component } from 'react';
import igv from 'igv/dist/igv.esm.min.js';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import Moment from 'moment';
import PreviousPageIcon from './PreviousPageIcon';
import Cookie from "js-cookie";

class IgvBrowser extends Component {
    constructor(props){
      super(props);
      this.state = {
        item: (this.props.location.state ? this.props.location.state.item : null)
      }
    }

    componentDidMount() {
      if(this.state.item) {
        var igvContainer = document.getElementById('igv-div');
        var igvOptions =
        {
            reference: {
                id: this.state.item.name,
                fastaURL: this.state.item.referenceUrl,
                headers: {"Authorization": 'Bearer ' + Cookie.get("jwtToken")}
            },
            tracks: this.getTracks()
        };

        return igv.createBrowser(igvContainer, igvOptions);
      }
    }

    getTracks(){
      var tracksForIgv = [];
      for(var x = 0; x < this.state.item.bamUrls.length; x++) {
        tracksForIgv.push({
          "url": this.state.item.bamUrls[x].url,
          indexed: true,
          format: "bam",
          name: this.state.item.bamUrls[x].name,
          headers: {"Authorization": 'Bearer ' + Cookie.get("jwtToken")}
        });
      }
      console.log(tracksForIgv);
      return tracksForIgv;
    }

    addEditButton(){
      if(localStorage.getItem("username") === this.state.item.owner || localStorage.getItem("role") === 'ADMIN')
      {
        return(
          <button className='btn btn-outline-secondary btn-lg w-25' onClick={ () => this.props.history.push('/alignments/edit',  {item : this.state.item})}>Edit</button>
        );
      }
    }

    addUpdated() {
      if(this.state.item.updatedAt) {
        return(            
          <>
          <p className="card-text">Updated At: {this.state.item.updatedAt ? Moment(this.state.item.updatedAt).format("YYYY.MM.DD. HH:mm") : ""}</p>
          <p className="card-text">Updated By: {this.state.item.updatedBy}</p>
          </>
          );
      }
    }
  
    render() {
      if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
        if(!this.state.item) {
          return(
              <Redirect to="/alignments" />
          );
        }
        return (<div className="container">
            <NavBar/>
            <div className="container">
              <div className="container">
                <PreviousPageIcon
                            where={'/alignments'}
                            hist={this.props.history}
                        />
                <h1 className="w-75 d-inline-block">IGV Genome browser</h1>
                {this.addEditButton()}
              </div>
              <br/>
              <div className="card">
                  <h5 className="card-header" style={{backgroundColor: "#e3f2fd"}}>{this.state.item.name}</h5>
                  <div className="card-body">
                    <div className="row">
                      <div className="col">
                        <p className="card-text">Aligner: {this.state.item.aligner}</p>
                        <p className="card-text">Owner: {this.state.item.owner}</p>
                        <p className="card-text">Visibility: {this.state.item.visibility}</p>
                      </div>
                      <div className="col">
                        <p className="card-text">Created At: {this.state.item.createdAt ? Moment(this.state.item.createdAt).format("YYYY.MM.DD. HH:mm") : ""}</p>
                        {this.addUpdated()}
                      </div>
                    </div>
                    <p className="card-text" style={{marginTop: "1rem"}}>Description: {this.state.item.description}</p>
                  </div>
              </div>
              <br/>
              <div className='igvContainer'>
                  <div id="igv-div"></div>
              </div>
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

  export default IgvBrowser;