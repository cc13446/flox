'use client'
import React, { useEffect } from 'react';

import Fetch, { IRequestParams, BodyType } from '../../lib/backend';

const App: React.FC = () => {

    useEffect(() => {
        console.log('Action has mounted.');
        return () => {
            console.log('Action will unmount.');
        };
    }, []);


    return (
        <>
            <h1>Action</h1>
        </>
    );
};

export default App;