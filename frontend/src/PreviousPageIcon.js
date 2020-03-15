import React from 'react';

class PreviousPageIcon extends React.Component{

    onClick(e) {
        this.props.hist.push(this.props.where);
    }

    render() {
        return(
            <div className="container">
                <i className="previousIcon" data-toogle="tooltip" data-placement="right" title="Previous page" onClick={(e) => this.onClick(e)}></i>
            </div>
        )
    }

}

export default PreviousPageIcon;
