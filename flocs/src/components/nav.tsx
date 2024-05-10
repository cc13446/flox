'use client';
import React, { useEffect, useState } from 'react';
import {
    ApartmentOutlined, ApiOutlined, ControlOutlined, CloudOutlined, DatabaseOutlined, DeploymentUnitOutlined,
    ForkOutlined, SlidersOutlined, SwapOutlined
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { Menu } from 'antd'
import ContentEnum from '../lib/contentEnum'

type MenuItem = Required<MenuProps>['items'][number];

function getItem(
    label: React.ReactNode,
    key: React.Key,
    icon?: React.ReactNode,
    children?: MenuItem[],
    type?: 'group',
): MenuItem {
    return {
        key,
        icon,
        children,
        label,
        type,
    } as MenuItem;
}

const items: MenuProps['items'] = [
    getItem(<p className='select-none'>数据源</p>, 'dataSource', <DatabaseOutlined />, [
        getItem(<p className='select-none'>连接配置</p>, ContentEnum.CONNECT, <ApiOutlined />),
        getItem(<p className='select-none'>动作配置</p>, ContentEnum.ACTION, <ControlOutlined />)
    ]),

    { type: 'divider' },

    getItem(<p className='select-none'>数据流</p>, 'flow', <SlidersOutlined />, [
        getItem(<p className='select-none'>数据类型定义</p>, ContentEnum.DATATYPE, <DeploymentUnitOutlined />),
        getItem(<p className='select-none'>节点定义</p>, ContentEnum.NODE, <SwapOutlined />),
        getItem(<p className='select-none'>子流程配置</p>, ContentEnum.SUBFLOX, <ForkOutlined />),
        getItem(<p className='select-none'>流程配置</p>, ContentEnum.FLOX, <ApartmentOutlined />),
        getItem(<p className='select-none'>端点配置</p>, ContentEnum.ENDPOINT, <CloudOutlined />),
    ]),

    { type: 'divider' },

];

interface Props {
    setActiveContent: React.Dispatch<React.SetStateAction<ContentEnum>>,
    activeContent: ContentEnum
}

const App: React.FC<Props> = (props: Props) => {

    const setActiveContent = props.setActiveContent;
    const onClick: MenuProps['onClick'] = (e) => {
        let content = e.key;
        setActiveContent(ContentEnum[content.toUpperCase() as keyof typeof ContentEnum]);
    };

    const [selectKey, setSelectKey] = useState<ContentEnum>(ContentEnum.HEAD);

    useEffect(() => {
        setSelectKey(props.activeContent)
    }, [props.activeContent]);


    return (
        <Menu
            onClick={onClick}
            style={{ width: 256, height: '100%' }}
            mode="inline"
            selectedKeys={[selectKey]}
            items={items}
        />
    );
};

export default App;