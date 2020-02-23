import React, { Component } from 'react';
import igv from 'igv/dist/igv.esm.min.js';

class IgvBrowser extends Component {

    componentDidMount() {
      var igvContainer = document.getElementById('igv-div');
      var igvOptions = 
      {
        showNavigation: true,
        showRuler: true,
        genome: "hg19",
        locus: 'chr7',
        tracks: [
            {
                url: 'https://data.broadinstitute.org/igvdata/test/igv-web/segmented_data_080520.seg.gz',
                indexed: false,
                isLog: true,
                name: 'Segmented CN'
            }
        ]
    };

      return igv.createBrowser(igvContainer, igvOptions);
    }
  
    render() {
      return (
          <div className='igvContainer'>
              <div id="igv-div"></div>
          </div>
      );
    }
  }

  export default IgvBrowser;