import React, { Component } from 'react';
import igv from 'igv/dist/igv.esm.min.js';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import Moment from 'moment';
import PreviousPageIcon from './PreviousPageIcon';
import Cookie from "js-cookie";
import {checkJwtToken} from './Common';

let download = require('downloadjs/download.min.js');

class IgvBrowser extends Component {
    constructor(props){
      super(props);
      this.state = {
        isLoggedIn: true,
        item: (this.props.location.state ? this.props.location.state.item : null),
        open: false
      }

    }

    async componentDidMount() {
      this.setState({isLoggedIn: await checkJwtToken()});
      if(this.state.item) {
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
          <div className="p-2 w-25">
            <button className='btn btn-outline-secondary btn-lg w-100' onClick={ () => this.props.history.push('/alignments/edit',  {item : this.state.item})}>Edit</button>
          </div>
        );
      }
    }

    addUpdated() {
      if(this.state.item.updatedAt) {
        return(            
          <>
          <p className="card-text"><strong>Updated At:</strong> {this.state.item.updatedAt ? Moment(this.state.item.updatedAt).format("YYYY.MM.DD. HH:mm") : ""}</p>
          <p className="card-text"><strong>Updated By:</strong> {this.state.item.updatedBy}</p>
          </>
          );
      }
    }

    async downloadBamFile(url) {
      return fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': 'Bearer ' + Cookie.get("jwtToken")
        }
      }).then(function(resp) {
        return resp.blob();
      }).then(function(blob) {
        download(blob, url.substring(url.lastIndexOf("/"), url.length));
      });
    }
  
    render() {
      if(this.state.isLoggedIn) {
        if(!this.state.item) {
          return(
              <Redirect to="/alignments" />
          );
        }
        return (<div className="container">
            <NavBar active="alignments"/>
            <div className="container">
              <div className="container">
                <PreviousPageIcon
                            where={'/alignments'}
                            hist={this.props.history}
                        />
                <div className="d-inline-flex justify-content-between" style={{width: "95%"}}>
                  <div className="p-2"><h1 className="d-inline-block">IGV Genome browser</h1></div>
                  {this.addEditButton()}
                </div>
              </div>
              <br/>
              <div className="accordion md-accordion" id="accordionEx" role="tablist" aria-multiselectable="true">
                <div className="card">
                  <div className="card-header" role="tab" id="headingOne1" style={{backgroundColor: "#e3f2fd"}} 
                  onClick={() => this.setState({open: !this.state.open})}>
                    <a data-toggle="collapse" data-parent="#accordionEx" href="#collapseOne1" aria-expanded="true" aria-controls="collapseOne1">
                      <h5 className="mb-0">
                      {this.state.item.name} <i className={"fa fa-angle-" + (this.state.open ? "down" : "right")}></i>
                      </h5>
                    </a>
                  </div>
                  <div id="collapseOne1" className={"collapse " + (this.state.open ? "show" : "")} role="tabpanel" aria-labelledby="headingOne1"
                    data-parent="#accordionEx">
                    <div className="card-body">
                      <div className="row">
                        <div className="col">
                          <p className="card-text"><strong>Aligner:</strong> {this.state.item.aligner}</p>
                          <p className="card-text"><strong>Owner:</strong> {this.state.item.owner}</p>
                          <p className="card-text"><strong>Visibility:</strong> {this.state.item.visibility}</p>
                        </div>
                        <div className="col">
                          <p className="card-text"><strong>Created At:</strong> {this.state.item.createdAt ? Moment(this.state.item.createdAt).format("YYYY.MM.DD. HH:mm") : ""}</p>
                          {this.addUpdated()}
                        </div>
                      </div>
                      <p className="card-text" style={{marginTop: "1rem"}}><strong>Description:</strong> {this.state.item.description}</p>
                      <p className="card-text mb-0"><strong>Download bam files:</strong> </p>
                      <div className="d-flex flex-wrap">
                        {this.state.item.bamUrls.map(function(bam,index) {
                          return <div className="p-2" key={index}>
                            <button className="btn btn-outline-primary" onClick={() => this.downloadBamFile(bam.url)}>Track: {bam.name}</button>
                            </div>
                        }, this)}
                      </div>
                    </div>
                  </div>
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