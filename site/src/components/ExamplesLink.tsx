import Link from "@docusaurus/Link";
import React from "react";
import {koneBranch} from "@site/inputData";

interface ExamplesLink {
    mainModuleName: string
    children: any
}

export default function ExamplesLink ({mainModuleName, children}: ExamplesLink): React.ReactElement {
    return (
        <Link href={`https://github.com/lounres/Kone/tree/${koneBranch}/libs/main/${mainModuleName}/examples`}>
            {children}
        </Link>
    )
}