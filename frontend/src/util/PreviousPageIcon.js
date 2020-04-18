import React from 'react';

class PreviousPageIcon extends React.Component{

    onClick(e) {
        if(this.props.item !== null)
            this.props.hist.push(this.props.where, {item : this.props.item});
        else
            this.props.hist.push(this.props.where);
    }

    render() {
        return(
            <i className="fa fa-angle-left fa-2x mr-3 pointer" data-toogle="tooltip" data-placement="right" title="Previous page" onClick={(e) => this.onClick(e)}></i>
        )
    }

}

export default PreviousPageIcon;
