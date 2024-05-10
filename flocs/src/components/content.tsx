import React from 'react';
import Head from './content/head'
import Connect from './content/connect'
import Action from './content/action'
import DataType from './content/dataType'
import Node from './content/node'
import SubFlox from './content/subFlox'
import Flox from './content/flox'
import Endpoint from './content/endpoint'
import ContentEnum from '../lib/contentEnum'

interface Props {
    activeContent: ContentEnum,
}

const App: React.FC<Props> = (props: Props) => {

    const renderComponent = () => {
        switch (props.activeContent) {
            case ContentEnum.HEAD:
                return <Head />;
            case ContentEnum.CONNECT:
                return <Connect />;
            case ContentEnum.ACTION:
                return <Action />;
            case ContentEnum.DATATYPE:
                return <DataType />;
            case ContentEnum.NODE:
                return <Node />;
            case ContentEnum.SUBFLOX:
                return <SubFlox />;
            case ContentEnum.FLOX:
                return <Flox />;
            case ContentEnum.ENDPOINT:
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