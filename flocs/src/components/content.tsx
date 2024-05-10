import React from 'react';
import Head from './content/head'
import Connect from './content/connect'
import Action from './content/action'
import DataType from './content/dataType'
import Node from './content/node'
import SubFlox from './content/subFlox'
import Flox from './content/flox'
import Endpoint from './content/endpoint'


interface Props {
    activeContent: String,
}

const App: React.FC<Props> = (props: Props) => {

    const renderComponent = () => {
        switch (props.activeContent) {
            case 'head':
                return <Head />;
            case 'connect':
                return <Connect />;
            case 'action':
                return <Action />;
            case 'dataType':
                return <DataType />;
            case 'node':
                return <Node />;
            case 'subFlox':
                return <SubFlox />;
            case 'flox':
                return <Flox />;
            case 'endpoint':
                return <Endpoint />;
            default:
                return null;
        }
    };

    return (
        <div className='p-5'>
            {renderComponent()}
        </div>
    );
};

export default App;