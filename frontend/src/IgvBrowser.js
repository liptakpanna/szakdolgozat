import React, { Component } from 'react';
import igv from 'igv/dist/igv.esm.min.js';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';

class IgvBrowser extends Component {

    componentDidMount() {
      var igvContainer = document.getElementById('igv-div');
      var igvOptions =
      {
          reference: {
              id: this.props.location.state.item.name,
              fastaURL: this.props.location.state.item.referenceUrl,
          },
          tracks: [
              {
                  url: this.props.location.state.item.bamUrl,
                  indexed: true,
                  format: "bam",
                  name: this.props.location.state.item.name + " read"
              }
          ]
      };

      return igv.createBrowser(igvContainer, igvOptions);
    }

    addEditButton(){
    
      if(localStorage.getItem("username") === this.props.location.state.item.owner)
      {
        return(
          <button className='btn btn-outline-secondary btn-lg w-25' onClick={ () => this.props.history.push('/alignments/edit')}>Edit</button>
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
                        <p className="card-text">Updated at: 2019.05.05. 26:45</p>
                      </div>
                    </div>
                    <p className="card-text" style={{marginTop: "1rem"}}>Description: {this.props.location.state.item.description}</p>
                    <a href={this.props.location.state.item.bamUrl} className="btn btn-primary">Download result bam file</a>
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