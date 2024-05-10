'use client'
import React, { useState } from 'react';
import Nav from '../components/nav'
import Head from '../components/head'
import Content from '../components/content'
import ContentEnum from '../lib/contentEnum'

const home = () => {
    const [activeContent, setActiveContent] = useState<ContentEnum>(ContentEnum.HEAD);

    return (
        <div className='h-dvh flex flex-col'>
            <div className='h-[4rem]'>
                <Head setActiveContent={setActiveContent} />
            </div>
            <div className='h-[55rem] flex flex-row'>
                <div className='h-full w-[16rem]'>
                    <Nav setActiveContent={setActiveContent} activeContent={activeContent} />
                </div>
                <div className='w-[104rem] overflow-scroll'>
                    <Content activeContent={activeContent} />
                </div>
            </div>
        </div>
    );
};

export default home;