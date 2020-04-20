export default class JCTool {
    
    public static request(requestObject: RequestObject) {
        requestObject.async = (requestObject.async == undefined ? true : requestObject.async);
        requestObject.method = (requestObject.method == undefined ? RequestMethod.GET : requestObject.method);
        requestObject.data = (requestObject.data == undefined ? {} : requestObject.data);
        requestObject.dataType = (requestObject.dataType == undefined ? DataType.FORM : requestObject.dataType);
        requestObject.responeType = (requestObject.responeType == undefined ? ResponeType.Object : requestObject.responeType);

        var xhr = new XMLHttpRequest();

        xhr.onreadystatechange = function() {
            if (xhr.readyState < 4) {
                return;
            }
            if (xhr.status >= 200 && xhr.status < 400) {
                if (requestObject.success instanceof Function) {
                    if (requestObject.responeType == ResponeType.Object) {
                        try {
                            requestObject.success(JSON.parse(xhr.responseText));
                        } catch {
                            requestObject.success(xhr.responseText);
                        }
                    } else if (requestObject.responeType == ResponeType.String) {
                        requestObject.success(xhr.responseText);
                    }
                }
            } else {
                if (requestObject.fail instanceof Function) {
                    requestObject.fail();
                }
            }
        };

        var getUrlForm = function(dataObject:any, startChar:string, centerChar:string): string {
            let str = ""; 
            if (dataObject) {
                let paramIndex = -1;
                for (let key in dataObject) {
                    paramIndex++;
                    str += (paramIndex == 0 ? startChar : centerChar);
                    str += key + '=' + encodeURIComponent(dataObject[key]);
                }
            }
            return str;
        }

        if (requestObject.method == RequestMethod.GET) {
            xhr.open("GET", requestObject.url + getUrlForm(requestObject.data, '?', '&'), requestObject.async);
            xhr.setRequestHeader("Content-Type" , "application/x-www-form-urlencoded");
            xhr.send();
        } else if (requestObject.method == RequestMethod.POST) {
            xhr.open("POST", requestObject.url, requestObject.async);
            if (requestObject.dataType == DataType.FORM) {
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                xhr.send(getUrlForm(requestObject.data, '', '&'));
            } else if (requestObject.dataType == DataType.JSON) {
                xhr.setRequestHeader("Content-Type", "application/json");
                xhr.send(JSON.stringify(requestObject.data));
            }
        }
    }
}
export interface RequestObject {
    url: string;
    async?: boolean;
    method?: RequestMethod;
    data?: any;
    dataType?: DataType;
    responeType?: ResponeType;
    success?: Function;
    fail?: Function;
}
export enum DataType {FORM, JSON}
export enum RequestMethod {GET, POST}
export enum ResponeType {Object, String}