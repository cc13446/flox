'use client';
import React from 'react';
import {
    ApartmentOutlined, ApiOutlined, ControlOutlined, DatabaseOutlined, DeploymentUnitOutlined,
    ForkOutlined, SlidersOutlined, SwapOutlined
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { Menu } from 'antd'

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
        getItem(<p className='select-none'>连接配置</p>, 'connect', <ApiOutlined />),
        getItem(<p className='select-none'>动作配置</p>, 'action', <ControlOutlined />)
    ]),

    { type: 'divider' },

    getItem(<p className='select-none'>数据流</p>, 'flow', <SlidersOutlined />, [
        getItem(<p className='select-none'>数据类型定义</p>, 'dataType', <DeploymentUnitOutlined />),
        getItem(<p className='select-none'>节点定义</p>, 'node', <SwapOutlined />),
        getItem(<p className='select-none'>子流程配置</p>, 'subFlox', <ForkOutlined />),
        getItem(<p className='select-none'>流程配置</p>, 'flox', <ApartmentOutlined />),
    ]),

    { type: 'divider' },

];

const App: React.FC = () => {
    const onClick: MenuProps['onClick'] = (e) => {
        console.log('click ', e);
    };

    return (
        <Menu
            onClick={onClick}
            style={{ width: 256, height: '100%' }}
            mode="inline"
            items={items}
        />
    );
};

export default App;