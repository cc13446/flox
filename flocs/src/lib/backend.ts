import axios, { AxiosHeaders, AxiosRequestConfig, AxiosResponse, Method } from 'axios';

const backUrl = 'http://127.0.0.1:8080'

const api = axios.create({
    baseURL: backUrl
});

export interface IRequestParams {
    path: string,
    method: Method,
    bodyType: BodyType,
    data: any,
}

export interface IResponseData {
    code: number,
    message: string,
    data: any,
}

export enum BodyType {
    FORM = 'application/x-www-form-urlencoded',
    JSON = 'application/json'
}

export default async (params: IRequestParams): Promise<any> => {
    try {
        const headers = new AxiosHeaders();
        headers.set('Content-Type', params.bodyType);
        headers.set('Access-Control-Allow-Origin', '*');
        const config: AxiosRequestConfig<any> = {
            url: params.path,
            method: params.method,
            headers: headers,
            data: params.data
        }
        const response: AxiosResponse<IResponseData> = await api.request(config);
        const data = response.data;
        if (data.code !== 200) {
            throw new Error(`Request failed with server error: ${data.message}`);
        }
        return data.data;
    } catch (error) {
        if (axios.isAxiosError(error)) {
            throw new Error(`Request failed with status code: ${error.response?.status}`);
        } else {
            throw error;
        }
    }
};