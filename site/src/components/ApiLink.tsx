import Link from "@docusaurus/Link";
import React from "react";
import {koneBaseUrl, koneUrl} from "@site/inputData"

interface ApiLink {
    to: string
    children: any
}

export default function ApiLink ({to, children}: ApiLink): React.ReactElement {
    return (
        <Link href={`${koneUrl}${koneBaseUrl}api/${to}`}>
            {children}
        </Link>
    )
}