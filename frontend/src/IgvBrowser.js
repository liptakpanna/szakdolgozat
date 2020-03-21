import React, { Component } from 'react';
import igv from 'igv/dist/igv.esm.min.js';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';
import Moment from 'moment';
import PreviousPageIcon from './PreviousPageIcon';

class IgvBrowser extends Component {

    componentDidMount() {
      var igvContainer = document.getElementById('igv-div');
      var igvOptions =
      {
          reference: {
              id: this.props.location.state.item.name,
              fastaURL: this.props.location.state.item.referenceUrl,
              headers: {"Authorization": 'Bearer ' + localStorage.getItem("jwtToken")}
          },
          tracks: this.getTracks()
      };

      return igv.createBrowser(igvContainer, igvOptions);
    }

    getTracks(){
      var tracksForIgv = [];
      for(var x = 0; x < this.props.location.state.item.bamUrls.length; x++) {
        tracksForIgv.push({
          "url": this.props.location.state.item.bamUrls[x].url,
          indexed: true,
          format: "bam",
          name: this.props.location.state.item.bamUrls[x].name,
          headers: {"Authorization": 'Bearer ' + localStorage.getItem("jwtToken")}
        });
      }
      console.log(tracksForIgv);
      return tracksForIgv;
    }

    addEditButton(){
      if(localStorage.getItem("username") === this.props.location.state.item.owner || localStorage.getItem("role") === 'ADMIN')
      {
        return(
          <button className='btn btn-outline-secondary btn-lg w-25' onClick={ () => this.props.history.push('/alignments/edit',  {item : this.props.location.state.item})}>Edit</button>
        );
      }
    }

    addUpdated() {
      if(this.props.location.state.item.updatedAt) {
        return(            
          <>
          <p className="card-text">Updated At: {this.props.location.state.item.updatedAt ? Moment(this.props.location.state.item.updatedAt).format("YYYY.MM.DD. HH:mm") : ""}</p>
          <p className="card-text">Updated By: {this.props.location.state.item.updatedBy}</p>
          </>
          );
      }
    }
  
    render() {
      if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
        return (
          <div className="container">
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
                  <h5 className="card-header" style={{backgroundColor: "#e3f2fd"}}>{this.props.location.state.item.name}</h5>
                  <div className="card-body">
                    <div className="row">
                      <div className="col">
                        <p className="card-text">Aligner: {this.props.location.state.item.aligner}</p>
                        <p className="card-text">Owner: {this.props.location.state.item.owner}</p>
                        <p className="card-text">Visibility: {this.props.location.state.item.visibility}</p>
                      </div>
                      <div className="col">
                        <p className="card-text">Created At: {this.props.location.state.item.createdAt ? Moment(this.props.location.state.item.createdAt).format("YYYY.MM.DD. HH:mm") : ""}</p>
                        {this.addUpdated()}
                      </div>
                    </div>
                    <p className="card-text" style={{marginTop: "1rem"}}>Description: {this.props.location.state.item.description}</p>
                    {/*
                    <a href={this.props.location.state.item.referenceUrl} className="btn btn-primary mr-3">Download reference file</a>
                    <a href={this.props.location.state.item.bamUrl} className="btn btn-primary">Download result bam file</a>
                    */} 
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
              <Redirect to="login" />
          );
      }
    }
  }

  export default IgvBrowser;