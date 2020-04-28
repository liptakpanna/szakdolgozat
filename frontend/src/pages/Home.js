import React from 'react';
import NavBar from '../util/NavBar';
import { Redirect } from 'react-router-dom';
import {checkJwtToken} from '../util/Common';

class Home extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            isLoggedIn : true,
        }
        this.role = localStorage.getItem("role");
    }

    componentDidMount(){
        this.setState({isLoggedIn: checkJwtToken()});
        console.log("home" + this.state.isLoggedIn);
    }

    addCreateAlignmentsText(){
        if(this.role!=="GUEST")
            return(<>
                <p>With this application you can use some of the most popular short read aligners.</p>
                <p>You can choose between three aligners: </p>
                <ul>
                    <li><a className="link" href="http://bowtie-bio.sourceforge.net/manual.shtml#what-is-bowtie">Bowtie:</a> "Bowtie is an ultrafast, memory-efficient short read aligner geared toward quickly aligning large sets of short DNA sequences (reads) to large genomes."</li>
                    <li><a className="link" href="http://bio-bwa.sourceforge.net/">BWA:</a> "BWA is a software package for mapping low-divergent sequences against a large reference genome, such as the human genome."</li>
                    <li><a className="link" href="http://snap.cs.berkeley.edu/">Snap:</a> "SNAP is a fast and accurate aligner for short DNA reads. It is optimized for modern read lengths of 100 bases or higher, and takes advantage of these reads to align data quickly through a hash-based indexing scheme."</li>
                </ul>
                <p>You can add different parameters to the different aligners.</p>
                <p>For creating an alignment you will need a reference genome (FASTA file) and one or more read files (FASTQ/FASTA files).</p>
                </>)
    }

    addAdminText(){
        if(this.role==="ADMIN")
            return(<>
                <p>As an Admin you can edit/delete and add users in the <strong>Users</strong> page.</p>
                </>)
    }

    render() {
        if(this.state.isLoggedIn && localStorage.getItem("username") !== "") {
            return(
                <div className="container">
                    <NavBar active="home"/>
                    <div className="container">
                        <h1>Introduction</h1>
                        <p>The data generated by next-generation sequencing instruments comprise huge numbers of very short DNA sequences, or ’reads’, that carry little information by themselves. These reads therefore have to be pieced together by well-engineered algorithms to reconstruct biologically meaningful measurments, such as the level of expression of a gene. To solve this complex, high-dimensional puzzle, reads must be mapped back to a reference genome to determine their origi.n Due to sequencing errors and to genuine differences between the reference genome and the individual being sequenced, this mapping process must be tolerant of mismatches, insertions, and deletions.
                             <a className="link" href="https://www.ncbi.nlm.nih.gov/pmc/articles/PMC5425171/">(Short Read Mapping: An Algorithmic Tour)</a> </p>
                        <p>In the <strong>Alignments</strong> page you can view the already existing alignments in the <strong>IGV genome browser</strong> or you can download the result BAM files.</p>
                        {this.addCreateAlignmentsText()}
                        <p>If you want to change your personal data you can do this in the <strong>Profile</strong> page.</p>
                        {this.addAdminText()}
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

export default Home;