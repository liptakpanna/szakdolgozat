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
              id: "My Custom Ecoli",
              fastaURL: "http://localhost:9090/resources/files/references/gyerunk.fna",
          },
          tracks: [
              {
                  url: "http://localhost:9090/resources/files/bams/gyerunk.bam",
                  indexed: true,
                  format: "bam",
                  name: 'Ecoli Sample'
              }
          ]
      };

      return igv.createBrowser(igvContainer, igvOptions);
    }
  
    render() {
      if(JSON.parse(localStorage.getItem("isLoggedIn"))) {
        return (
          <div className="container">
            <NavBar/>
            <div className='igvContainer'>
                <div id="igv-div"></div>
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