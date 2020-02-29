import React, { Component } from 'react';
import igv from 'igv/dist/igv.esm.min.js';
import NavBar from './NavBar';
import { Redirect } from 'react-router-dom';

class IgvBrowser extends Component {

    componentDidMount() {
      var igvContainer = document.getElementById('igv-div');
      var igvOptions = 
      {
        genome: "hg38",
        locus: "chr8:127,736,588-127,739,371",
        tracks: [
            {
                "name": "HG00103",
                "url": "https://s3.amazonaws.com/1000genomes/data/HG00103/alignment/HG00103.alt_bwamem_GRCh38DH.20150718.GBR.low_coverage.cram",
                "indexURL": "https://s3.amazonaws.com/1000genomes/data/HG00103/alignment/HG00103.alt_bwamem_GRCh38DH.20150718.GBR.low_coverage.cram.crai",
                "format": "cram"
            }
        ]
    };

      return igv.createBrowser(igvContainer, igvOptions);
    }
  
    render() {
      if(true) {
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