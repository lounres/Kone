import Link from "@docusaurus/Link";
import React from "react";
import {koneBaseUrl, koneUrl} from "@site/src/inputData";

interface ApiLink {
    to: string
    children: any
}

export default function ApiLink ({to, children}: ApiLink): JSX.Element {
    return (
        <Link href={`${koneUrl}${koneBaseUrl}api/${to}`}>
            {children}
        </Link>
    )
}