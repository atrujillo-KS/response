<?xml version="1.0" encoding="UTF-8"?>
<WebElementEntity>
   <description></description>
   <name>div_Results                                _bf5815</name>
   <tag></tag>
   <elementGuidId>d276fcf2-b3b1-4be0-985e-8f38c19d2147</elementGuidId>
   <selectorCollection>
      <entry>
         <key>CSS</key>
         <value>div.lf-meter.lf-meter-content</value>
      </entry>
      <entry>
         <key>XPATH</key>
         <value>//div[@id='lf_main']/div/div[3]</value>
      </entry>
   </selectorCollection>
   <selectorMethod>XPATH</selectorMethod>
   <smartLocatorEnabled>false</smartLocatorEnabled>
   <useRalativeImagePath>true</useRalativeImagePath>
   <webElementProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>tag</name>
      <type>Main</type>
      <value>div</value>
      <webElementGuid>594ccbd1-c1c8-4d91-937a-8b12d6a62048</webElementGuid>
   </webElementProperties>
   <webElementProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>class</name>
      <type>Main</type>
      <value>lf-meter lf-meter-content</value>
      <webElementGuid>2b43dafb-9d45-41eb-955e-e80c93708961</webElementGuid>
   </webElementProperties>
   <webElementProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>text</name>
      <type>Main</type>
      <value>
            
            
            
            
                
                    Results
                    
                        The used vehicle will cost you $89,910 less on average each year
                    
                    
                        The used vehicle will cost you $89,910 less on average each year

                        Show details

                        

                            
                                Results details
                                    
                                    
                                        
                                        New
                                        Used
                                    
                                    
                                    

                                    
                                        
                                            Average cost per year
                                        
                                        $93,103
                                        
                                        $3,193
                                        
                                    
                                    
                                        
                                            Monthly payment
                                        
                                        $20,039
                                        
                                        $233
                                        
                                    
                                    
                                        
                                            Total interest paid
                                        
                                        $73,563
                                        
                                        $1,163
                                        
                                    
                                    
                                        
                                            Total depreciation
                                        
                                        $375,379
                                        
                                        $5,588
                                        
                                    
                                    
                                
                            
                            
                                {{#text}}
                                &lt;div id=&quot;lf_answer_text&quot; class=&quot;tableCellADA&quot;>{{{text}}}&lt;/div>
                                {{/text}}
                                {{^text}}
                                {{#.}}
                                {{#title}}
                                &lt;div id=&quot;lf_result_title&quot; class=&quot;lf-result-title&quot;>{{{title}}}&lt;/div>
                                {{/title}}
                                &lt;div class=&quot;lf-results-list lf-list-{{listNumber}}&quot;>
                                    {{#entries}}
                                    &lt;div id=&quot;lf_{{dataElementName}}_results&quot; class=&quot;lf-result-list-content&quot;>
                                        &lt;div id=&quot;lf_{{dataElementName}}_result_label&quot; class=&quot;lf-details-list-label&quot;>
                                            {{{fieldLabel}}}
                                        &lt;/div>
                                        {{#formattedValues}}
                                        &lt;div id=&quot;lf_{{dataElementName}}_result_value&quot;
                                             class=&quot;lf-details-list-value&quot;>{{value}}
                                        &lt;/div>
                                        {{/formattedValues}}
                                    &lt;/div>
                                    {{/entries}}
                                &lt;/div>
                                {{/.}}
                                {{/text}}
                            
                            
                                {{#text}}
                                &lt;div id=&quot;lf_answer_text&quot; class=&quot;tableCellADA&quot;>{{{text}}}&lt;/div>
                                {{/text}}
                                {{^text}}
                                {{#.}}
                                {{#title}}
                                &lt;div id=&quot;lf_result_title&quot; class=&quot;tableCellADA&quot; class=&quot;lf-result-title&quot;>{{{title}}}&lt;/div>
                                {{/title}}
                                &lt;table class=&quot;lf-results-table&quot; id=&quot;lf-results-table-main&quot;>
                                    &lt;thead>
                                    &lt;tr>
                                        {{#columnHeaders}}
                                        &lt;td>&lt;/td>
                                        {{#columnHeader}}
                                        &lt;th class=&quot;lf-results-header tableCellADA&quot; scope=&quot;col&quot;>{{.}}&lt;/th>
                                        {{/columnHeader}}
                                        {{/columnHeaders}}
                                    &lt;/tr>
                                    &lt;/thead>
                                    &lt;tbody>
                                    {{#subtitle}}
                                    &lt;tr>
                                        &lt;th id=&quot;lf_result_subtitle1&quot;
                                            class=&quot;lf-result-subtitle tableCellADA three-headers&quot; colspan=&quot;100%&quot;>
                                            {{{subtitle}}}
                                        &lt;/th>
                                    &lt;/tr>
                                    {{/subtitle}}

                                    {{#entries}}
                                    &lt;tr id=&quot;lf_{{dataElementName}}_results&quot;>
                                        &lt;th id=&quot;lf_{{dataElementName}}_result_label&quot; class=&quot;tableCellADA&quot; scope=&quot;row&quot;>
                                            {{#negLabel}}&lt;span class=&quot;lf-minus-sign&quot;>&amp;minus;&lt;/span>{{/negLabel}}{{{fieldLabel}}}
                                            {{#glossaryTerm}}
                                            &lt;a role=&quot;button&quot; href=&quot;#{{glossaryTerm}}&quot;
                                               id=&quot;lf_{{dataElementName}}_tooltip&quot;
                                               class=&quot;lf-show-tooltip lf_{{dataElementName}}_tooltip lf-more-details-tooltip&quot;
                                               {{#tooltipAriaLabel}}aria-label=&quot;{{{tooltipAriaLabel}}}&quot;{{/tooltipAriaLabel}}>
                                            &lt;div aria-hidden=&quot;true&quot; class=&quot;lf-tooltip-i lf-results-tooltip&quot;>i&lt;/div>
                                            &lt;/a>
                                            {{/glossaryTerm}}
                                        &lt;/th>
                                        {{#formattedValues}}
                                        &lt;td id=&quot;lf_{{dataElementName}}_result_value&quot;
                                            class=&quot;tableCellADA {{shownClasses}}&quot;>&lt;span class=&quot;lf-nbsp-number&quot;>{{#negSymbol}}&lt;span class=&quot;lf-minus-sign {{negSymbolClass}}&quot;>&amp;minus;&lt;/span>{{/negSymbol}}{{value}}&lt;/span>
                                        &lt;/td>
                                        {{/formattedValues}}
                                    &lt;/tr>
                                    {{/entries}}
                                    &lt;/tbody>
                                &lt;/table>
                                {{/.}}
                                {{/text}}
                            
                        

                    
                    Next Steps

                    
                        Enable color patterns
                            
                                Average cost per year of the new vehicle and the used vehicleBar chart with 2 data series.View as data table, Average cost per year of the new vehicle and the used vehicleThe chart has 1 X axis displaying categories. The chart has 1 Y axis displaying values. Data ranges from 3193 to 93103.Chart context menuAverage cost per year of the new vehicle and the used vehicleNewUsed0100k25k50k75kEnd of interactive chart.
                            
                            
                                
                            
                        
                        Change views between Average Cost graph, and Payments graph
                        
                            
                            
                                
                                Average Cost
                            
                            
                                
                                Payments
                            
                        
                    
                    
                        &lt;div class=&quot;lf-graph-list&quot;>
                            {{#.}}
                            {{^table}}
                            &lt;div id=&quot;{{id}}-graph-container&quot; class=&quot;lf-graph{{#first}} lf-active{{/first}}&quot; role=&quot;tabpanel&quot; aria-labelledby=&quot;lf_graph_tabs_{{id}}&quot;>
                                &lt;div id=&quot;{{id}}&quot; class=&quot;lf-cm-90 lf-graph_container&quot;>&lt;/div>
                            &lt;/div>
                            {{/table}}
                            {{#table}}
                            &lt;div id=&quot;{{id}}-graph-container&quot; class=&quot;lf-graph lf-hidden&quot; role=&quot;tabpanel&quot; aria-labelledby=&quot;lf_graph_tabs_{{id}}&quot;>
                                &lt;div class=&quot;lf-tables-links&quot;>
                                    {{#tables}}
                                    &lt;div>
                                        {{#label}}
                                        &lt;span class=&quot;lf-graph-label lf-graph-label-multiple&quot;>{{title}}&lt;/span>
                                        &lt;button id=&quot;lf_tables_links{{index}}&quot;
                                                class=&quot;lf-open-table lf-open-table-xl lf-open-table-multiple&quot;
                                                tabletitle=&quot;{{title}}&quot;
                                                lf-index=&quot;{{index}}&quot;
                                                lf-title=&quot;{{title}}&quot;>
                                            &lt;span aria-hidden=&quot;true&quot; class=&quot;lf-open-table-label&quot;>&lt;/span>
                                        &lt;/button>
                                        {{/label}}
                                        {{^label}}
                                        &lt;span class=&quot;lf-graph-label lf-graph-label-single&quot;>{{title}}&lt;/span>
                                        &lt;button id=&quot;lf_tables_links{{index}}&quot;
                                                class=&quot;lf-open-table lf-open-table-xl lf-open-table-single&quot;
                                                tabletitle=&quot;{{title}}&quot;
                                                lf-index=&quot;{{index}}&quot;
                                                lf-title=&quot;{{title}}&quot;>
                                            &lt;span aria-hidden=&quot;true&quot; class=&quot;lf-open-table-label&quot;>&lt;/span>
                                        &lt;/button>
                                        {{/label}}
                                    &lt;/div>
                                    {{/tables}}
                                &lt;/div>
                            &lt;/div>
                            {{/table}}
                            {{/.}}
                        &lt;/div>
                        &lt;div id=&quot;lf_selectors_description&quot; class=&quot;visually-hidden&quot;>&lt;/div>
                        &lt;div class=&quot;lf-results-loop&quot; role=&quot;tablist&quot; aria-labelledby=&quot;lf_selectors_description&quot;>
                            &lt;span class=&quot;lf-divider&quot;>&lt;/span>
                            {{#.}}
                            &lt;button id=&quot;lf_graph_tabs_{{id}}&quot; class=&quot;{{#first}}lf-active {{/first}}{{#last}}lf-last-loop{{/last}} lf-graph-tabs&quot; role=&quot;tab&quot; aria-selected=&quot;false&quot; aria-controls=&quot;{{id}}-graph-container&quot; tabindex=&quot;-1&quot;>
                                &lt;span class=&quot;lf-dot&quot; aria-hidden=&quot;true&quot;>&lt;/span>
                                &lt;div class=&quot;lf-dot-holder lf-dot-label&quot;>{{label}}&lt;/div>
                            &lt;/button>
                            {{/.}}
                        &lt;/div>
                    
                    
                        
                    
                

                
                
                    These calculators are intended to be used by Leadfusion for demonstration purposes only. Linking and usage of these calculators without a written license agreement is in violation of the Leadfusion Terms of Use and therefore not permitted. Please Click Here to go to GOOGLE. Please contact cs@leadfusion.com with any questions.
                

            

            
                Inputs
                A note about this tool: Some of the fields are pre-filled to get you started. Move the sliders or enter a number to change the amounts. The calculator results will automatically update as you move the sliders or leave a text field.
                
                
                    
    
            

                
                    
                        
                        New Vehicle Loan
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Purchase price
                                
                        
                        Must be between $0 and $1,000,000
                        
                            $
                            
                            %
                             
                        
                            Purchase price
                            
                            
                        


                        
                    
                    
                        
                            Down payment 
                            i
                        
                        Must be between $0 and $249,999
                        
                            $
                            
                            %
                             
                        
                            Down payment
                            
                            
                        


                        
                    
                    
                        
                            Interest rate
                                
                        
                        Must be between 0.00% and 50.00%
                        
                            $
                            
                            %
                             
                        
                            Interest rate
                            
                            
                        


                        
                    
                    
                        
                            Loan term (months)
                            i
                        
                        Must be between 1 and 360
                        
                            $
                            
                            %
                             
                        
                            Loan term
                            
                            
                        


                        
                    
                
            
            

                
                    
                        
                        New Vehicle Cost
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Years you will own vehicle
                                
                        
                        Must be between 1 and 30
                        
                            $
                            
                            %
                             
                        
                            Years you will own vehicle
                            
                            
                        


                        
                    
                    
                        
                            Monthly maintenance costs
                                
                        
                        Must be between $0 and $10,000
                        
                            $
                            
                            %
                             
                        
                            Monthly maintenance costs
                            
                            
                        


                        
                    
                    
                        
                            Monthly insurance costs
                                
                        
                        Must be between $0 and $10,000
                        
                            $
                            
                            %
                             
                        
                            Monthly insurance costs
                            
                            
                        


                        
                    
                    
                        
                            Registration costs
                                
                        
                        Must be between $0 and $100,000
                        
                            $
                            
                            %
                             
                        
                            Registration costs
                            
                            
                        


                        
                    
                
            
            

                
                    
                        
                        Used Vehicle Loan
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Purchase price
                                
                        
                        Must be between $0 and $250,000
                        
                            $
                            
                            %
                             
                        
                            Purchase price
                            
                            
                        


                        
                    
                    
                        
                            Down payment 
                            i
                        
                        Must be between $0 and $249,999
                        
                            $
                            
                            %
                             
                        
                            Down payment
                            
                            
                        


                        
                    
                    
                        
                            Interest rate
                                
                        
                        Must be between 0.00% and 50.00%
                        
                            $
                            
                            %
                             
                        
                            Interest rate
                            
                            
                        


                        
                    
                    
                        
                            Loan term  (months)
                            i
                        
                        Must be between 1 and 360
                        
                            $
                            
                            %
                             
                        
                            Loan term 
                            
                            
                        


                        
                    
                
            
            

                
                    
                        
                        Used Vehicle Cost
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Vehicle age
                                 (years)
                        
                        Must be between 0 and 30
                        
                            $
                            
                            %
                             
                        
                            Vehicle age
                            
                            
                        


                        
                    
                    
                        
                            Years you will own vehicle
                                
                        
                        Must be between 1 and 30
                        
                            $
                            
                            %
                             
                        
                            Years you will own vehicle
                            
                            
                        


                        
                    
                    
                        
                            Monthly maintenance costs
                                
                        
                        Must be between $0 and $10,000
                        
                            $
                            
                            %
                             
                        
                            Monthly maintenance costs
                            
                            
                        


                        
                    
                    
                        
                            Monthly insurance costs
                                
                        
                        Must be between $0 and $10,000
                        
                            $
                            
                            %
                             
                        
                            Monthly insurance costs
                            
                            
                        


                        
                    
                    
                        
                            Registration costs
                                
                        
                        Must be between $0 and $100,000
                        
                            $
                            
                            %
                             
                        
                            Registration costs
                            
                            
                        


                        
                    
                
            
            

                
                    
                        
                        Taxes &amp; Depreciation
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Your state &amp; federal tax rate 
                            i
                        
                        Must be between 0.00% and 90.00%
                        
                            $
                            
                            %
                             
                        
                            Your state &amp; federal tax rate
                            
                            
                        


                        
                    
                    
                        
                            Future depreciation 
                            i
                        
                        Error message for dropdown list here
                        
                            
                                Low

                                Average

                                High
                            


                        
                    
                    
                        
                            Your savings interest rate
                                
                        
                        Must be between 0.00% and 100.00%
                        
                            $
                            
                            %
                             
                        
                            Your savings interest rate
                            
                            
                        


                        
                    
                
            
        

                
            

            
                
                These calculators are intended to be used by Leadfusion for demonstration purposes only. Linking and usage of these calculators without a written license agreement is in violation of the Leadfusion Terms of Use and therefore not permitted. Please Click Here to go to GOOGLE. Please contact cs@leadfusion.com with any questions.
            

            
                
                    
                        
                            
                            Terms of use
                        
                    
                    © 2025 Leadfusion
                
            
        </value>
      <webElementGuid>c632479d-c07c-4ee9-b0d8-10e9124175cb</webElementGuid>
   </webElementProperties>
   <webElementProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath</name>
      <type>Main</type>
      <value>id(&quot;lf_main&quot;)/div[@class=&quot;lf-meter lf-main-inner&quot;]/div[@class=&quot;lf-meter lf-meter-content&quot;]</value>
      <webElementGuid>18f9513b-ab2a-4b4c-98cc-1a64369a5fbe</webElementGuid>
   </webElementProperties>
   <webElementXpaths>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath:idRelative</name>
      <type>Main</type>
      <value>//div[@id='lf_main']/div/div[3]</value>
      <webElementGuid>648121d5-cec8-445a-9f92-b9ed578b926a</webElementGuid>
   </webElementXpaths>
   <webElementXpaths>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath:neighbor</name>
      <type>Main</type>
      <value>(.//*[normalize-space(text()) and normalize-space(.)='In this tool, the results display before the inputs.'])[1]/following::div[1]</value>
      <webElementGuid>7c4c6a76-703d-4f56-8fdf-91cd2a39e3ec</webElementGuid>
   </webElementXpaths>
   <webElementXpaths>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath:neighbor</name>
      <type>Main</type>
      <value>(.//*[normalize-space(text()) and normalize-space(.)='Help'])[1]/following::div[2]</value>
      <webElementGuid>3bf26bf3-bb25-45ec-99db-cf8512d66f4a</webElementGuid>
   </webElementXpaths>
   <webElementXpaths>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath:position</name>
      <type>Main</type>
      <value>//div/div[3]</value>
      <webElementGuid>b891faff-1762-4ed2-a031-5946e9244f71</webElementGuid>
   </webElementXpaths>
   <webElementXpaths>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath:customAttributes</name>
      <type>Main</type>
      <value>//div[(text() = '
            
            
            
            
                
                    Results
                    
                        The used vehicle will cost you $89,910 less on average each year
                    
                    
                        The used vehicle will cost you $89,910 less on average each year

                        Show details

                        

                            
                                Results details
                                    
                                    
                                        
                                        New
                                        Used
                                    
                                    
                                    

                                    
                                        
                                            Average cost per year
                                        
                                        $93,103
                                        
                                        $3,193
                                        
                                    
                                    
                                        
                                            Monthly payment
                                        
                                        $20,039
                                        
                                        $233
                                        
                                    
                                    
                                        
                                            Total interest paid
                                        
                                        $73,563
                                        
                                        $1,163
                                        
                                    
                                    
                                        
                                            Total depreciation
                                        
                                        $375,379
                                        
                                        $5,588
                                        
                                    
                                    
                                
                            
                            
                                {{#text}}
                                &lt;div id=&quot;lf_answer_text&quot; class=&quot;tableCellADA&quot;>{{{text}}}&lt;/div>
                                {{/text}}
                                {{^text}}
                                {{#.}}
                                {{#title}}
                                &lt;div id=&quot;lf_result_title&quot; class=&quot;lf-result-title&quot;>{{{title}}}&lt;/div>
                                {{/title}}
                                &lt;div class=&quot;lf-results-list lf-list-{{listNumber}}&quot;>
                                    {{#entries}}
                                    &lt;div id=&quot;lf_{{dataElementName}}_results&quot; class=&quot;lf-result-list-content&quot;>
                                        &lt;div id=&quot;lf_{{dataElementName}}_result_label&quot; class=&quot;lf-details-list-label&quot;>
                                            {{{fieldLabel}}}
                                        &lt;/div>
                                        {{#formattedValues}}
                                        &lt;div id=&quot;lf_{{dataElementName}}_result_value&quot;
                                             class=&quot;lf-details-list-value&quot;>{{value}}
                                        &lt;/div>
                                        {{/formattedValues}}
                                    &lt;/div>
                                    {{/entries}}
                                &lt;/div>
                                {{/.}}
                                {{/text}}
                            
                            
                                {{#text}}
                                &lt;div id=&quot;lf_answer_text&quot; class=&quot;tableCellADA&quot;>{{{text}}}&lt;/div>
                                {{/text}}
                                {{^text}}
                                {{#.}}
                                {{#title}}
                                &lt;div id=&quot;lf_result_title&quot; class=&quot;tableCellADA&quot; class=&quot;lf-result-title&quot;>{{{title}}}&lt;/div>
                                {{/title}}
                                &lt;table class=&quot;lf-results-table&quot; id=&quot;lf-results-table-main&quot;>
                                    &lt;thead>
                                    &lt;tr>
                                        {{#columnHeaders}}
                                        &lt;td>&lt;/td>
                                        {{#columnHeader}}
                                        &lt;th class=&quot;lf-results-header tableCellADA&quot; scope=&quot;col&quot;>{{.}}&lt;/th>
                                        {{/columnHeader}}
                                        {{/columnHeaders}}
                                    &lt;/tr>
                                    &lt;/thead>
                                    &lt;tbody>
                                    {{#subtitle}}
                                    &lt;tr>
                                        &lt;th id=&quot;lf_result_subtitle1&quot;
                                            class=&quot;lf-result-subtitle tableCellADA three-headers&quot; colspan=&quot;100%&quot;>
                                            {{{subtitle}}}
                                        &lt;/th>
                                    &lt;/tr>
                                    {{/subtitle}}

                                    {{#entries}}
                                    &lt;tr id=&quot;lf_{{dataElementName}}_results&quot;>
                                        &lt;th id=&quot;lf_{{dataElementName}}_result_label&quot; class=&quot;tableCellADA&quot; scope=&quot;row&quot;>
                                            {{#negLabel}}&lt;span class=&quot;lf-minus-sign&quot;>&amp;minus;&lt;/span>{{/negLabel}}{{{fieldLabel}}}
                                            {{#glossaryTerm}}
                                            &lt;a role=&quot;button&quot; href=&quot;#{{glossaryTerm}}&quot;
                                               id=&quot;lf_{{dataElementName}}_tooltip&quot;
                                               class=&quot;lf-show-tooltip lf_{{dataElementName}}_tooltip lf-more-details-tooltip&quot;
                                               {{#tooltipAriaLabel}}aria-label=&quot;{{{tooltipAriaLabel}}}&quot;{{/tooltipAriaLabel}}>
                                            &lt;div aria-hidden=&quot;true&quot; class=&quot;lf-tooltip-i lf-results-tooltip&quot;>i&lt;/div>
                                            &lt;/a>
                                            {{/glossaryTerm}}
                                        &lt;/th>
                                        {{#formattedValues}}
                                        &lt;td id=&quot;lf_{{dataElementName}}_result_value&quot;
                                            class=&quot;tableCellADA {{shownClasses}}&quot;>&lt;span class=&quot;lf-nbsp-number&quot;>{{#negSymbol}}&lt;span class=&quot;lf-minus-sign {{negSymbolClass}}&quot;>&amp;minus;&lt;/span>{{/negSymbol}}{{value}}&lt;/span>
                                        &lt;/td>
                                        {{/formattedValues}}
                                    &lt;/tr>
                                    {{/entries}}
                                    &lt;/tbody>
                                &lt;/table>
                                {{/.}}
                                {{/text}}
                            
                        

                    
                    Next Steps

                    
                        Enable color patterns
                            
                                Average cost per year of the new vehicle and the used vehicleBar chart with 2 data series.View as data table, Average cost per year of the new vehicle and the used vehicleThe chart has 1 X axis displaying categories. The chart has 1 Y axis displaying values. Data ranges from 3193 to 93103.Chart context menuAverage cost per year of the new vehicle and the used vehicleNewUsed0100k25k50k75kEnd of interactive chart.
                            
                            
                                
                            
                        
                        Change views between Average Cost graph, and Payments graph
                        
                            
                            
                                
                                Average Cost
                            
                            
                                
                                Payments
                            
                        
                    
                    
                        &lt;div class=&quot;lf-graph-list&quot;>
                            {{#.}}
                            {{^table}}
                            &lt;div id=&quot;{{id}}-graph-container&quot; class=&quot;lf-graph{{#first}} lf-active{{/first}}&quot; role=&quot;tabpanel&quot; aria-labelledby=&quot;lf_graph_tabs_{{id}}&quot;>
                                &lt;div id=&quot;{{id}}&quot; class=&quot;lf-cm-90 lf-graph_container&quot;>&lt;/div>
                            &lt;/div>
                            {{/table}}
                            {{#table}}
                            &lt;div id=&quot;{{id}}-graph-container&quot; class=&quot;lf-graph lf-hidden&quot; role=&quot;tabpanel&quot; aria-labelledby=&quot;lf_graph_tabs_{{id}}&quot;>
                                &lt;div class=&quot;lf-tables-links&quot;>
                                    {{#tables}}
                                    &lt;div>
                                        {{#label}}
                                        &lt;span class=&quot;lf-graph-label lf-graph-label-multiple&quot;>{{title}}&lt;/span>
                                        &lt;button id=&quot;lf_tables_links{{index}}&quot;
                                                class=&quot;lf-open-table lf-open-table-xl lf-open-table-multiple&quot;
                                                tabletitle=&quot;{{title}}&quot;
                                                lf-index=&quot;{{index}}&quot;
                                                lf-title=&quot;{{title}}&quot;>
                                            &lt;span aria-hidden=&quot;true&quot; class=&quot;lf-open-table-label&quot;>&lt;/span>
                                        &lt;/button>
                                        {{/label}}
                                        {{^label}}
                                        &lt;span class=&quot;lf-graph-label lf-graph-label-single&quot;>{{title}}&lt;/span>
                                        &lt;button id=&quot;lf_tables_links{{index}}&quot;
                                                class=&quot;lf-open-table lf-open-table-xl lf-open-table-single&quot;
                                                tabletitle=&quot;{{title}}&quot;
                                                lf-index=&quot;{{index}}&quot;
                                                lf-title=&quot;{{title}}&quot;>
                                            &lt;span aria-hidden=&quot;true&quot; class=&quot;lf-open-table-label&quot;>&lt;/span>
                                        &lt;/button>
                                        {{/label}}
                                    &lt;/div>
                                    {{/tables}}
                                &lt;/div>
                            &lt;/div>
                            {{/table}}
                            {{/.}}
                        &lt;/div>
                        &lt;div id=&quot;lf_selectors_description&quot; class=&quot;visually-hidden&quot;>&lt;/div>
                        &lt;div class=&quot;lf-results-loop&quot; role=&quot;tablist&quot; aria-labelledby=&quot;lf_selectors_description&quot;>
                            &lt;span class=&quot;lf-divider&quot;>&lt;/span>
                            {{#.}}
                            &lt;button id=&quot;lf_graph_tabs_{{id}}&quot; class=&quot;{{#first}}lf-active {{/first}}{{#last}}lf-last-loop{{/last}} lf-graph-tabs&quot; role=&quot;tab&quot; aria-selected=&quot;false&quot; aria-controls=&quot;{{id}}-graph-container&quot; tabindex=&quot;-1&quot;>
                                &lt;span class=&quot;lf-dot&quot; aria-hidden=&quot;true&quot;>&lt;/span>
                                &lt;div class=&quot;lf-dot-holder lf-dot-label&quot;>{{label}}&lt;/div>
                            &lt;/button>
                            {{/.}}
                        &lt;/div>
                    
                    
                        
                    
                

                
                
                    These calculators are intended to be used by Leadfusion for demonstration purposes only. Linking and usage of these calculators without a written license agreement is in violation of the Leadfusion Terms of Use and therefore not permitted. Please Click Here to go to GOOGLE. Please contact cs@leadfusion.com with any questions.
                

            

            
                Inputs
                A note about this tool: Some of the fields are pre-filled to get you started. Move the sliders or enter a number to change the amounts. The calculator results will automatically update as you move the sliders or leave a text field.
                
                
                    
    
            

                
                    
                        
                        New Vehicle Loan
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Purchase price
                                
                        
                        Must be between $0 and $1,000,000
                        
                            $
                            
                            %
                             
                        
                            Purchase price
                            
                            
                        


                        
                    
                    
                        
                            Down payment 
                            i
                        
                        Must be between $0 and $249,999
                        
                            $
                            
                            %
                             
                        
                            Down payment
                            
                            
                        


                        
                    
                    
                        
                            Interest rate
                                
                        
                        Must be between 0.00% and 50.00%
                        
                            $
                            
                            %
                             
                        
                            Interest rate
                            
                            
                        


                        
                    
                    
                        
                            Loan term (months)
                            i
                        
                        Must be between 1 and 360
                        
                            $
                            
                            %
                             
                        
                            Loan term
                            
                            
                        


                        
                    
                
            
            

                
                    
                        
                        New Vehicle Cost
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Years you will own vehicle
                                
                        
                        Must be between 1 and 30
                        
                            $
                            
                            %
                             
                        
                            Years you will own vehicle
                            
                            
                        


                        
                    
                    
                        
                            Monthly maintenance costs
                                
                        
                        Must be between $0 and $10,000
                        
                            $
                            
                            %
                             
                        
                            Monthly maintenance costs
                            
                            
                        


                        
                    
                    
                        
                            Monthly insurance costs
                                
                        
                        Must be between $0 and $10,000
                        
                            $
                            
                            %
                             
                        
                            Monthly insurance costs
                            
                            
                        


                        
                    
                    
                        
                            Registration costs
                                
                        
                        Must be between $0 and $100,000
                        
                            $
                            
                            %
                             
                        
                            Registration costs
                            
                            
                        


                        
                    
                
            
            

                
                    
                        
                        Used Vehicle Loan
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Purchase price
                                
                        
                        Must be between $0 and $250,000
                        
                            $
                            
                            %
                             
                        
                            Purchase price
                            
                            
                        


                        
                    
                    
                        
                            Down payment 
                            i
                        
                        Must be between $0 and $249,999
                        
                            $
                            
                            %
                             
                        
                            Down payment
                            
                            
                        


                        
                    
                    
                        
                            Interest rate
                                
                        
                        Must be between 0.00% and 50.00%
                        
                            $
                            
                            %
                             
                        
                            Interest rate
                            
                            
                        


                        
                    
                    
                        
                            Loan term  (months)
                            i
                        
                        Must be between 1 and 360
                        
                            $
                            
                            %
                             
                        
                            Loan term 
                            
                            
                        


                        
                    
                
            
            

                
                    
                        
                        Used Vehicle Cost
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Vehicle age
                                 (years)
                        
                        Must be between 0 and 30
                        
                            $
                            
                            %
                             
                        
                            Vehicle age
                            
                            
                        


                        
                    
                    
                        
                            Years you will own vehicle
                                
                        
                        Must be between 1 and 30
                        
                            $
                            
                            %
                             
                        
                            Years you will own vehicle
                            
                            
                        


                        
                    
                    
                        
                            Monthly maintenance costs
                                
                        
                        Must be between $0 and $10,000
                        
                            $
                            
                            %
                             
                        
                            Monthly maintenance costs
                            
                            
                        


                        
                    
                    
                        
                            Monthly insurance costs
                                
                        
                        Must be between $0 and $10,000
                        
                            $
                            
                            %
                             
                        
                            Monthly insurance costs
                            
                            
                        


                        
                    
                    
                        
                            Registration costs
                                
                        
                        Must be between $0 and $100,000
                        
                            $
                            
                            %
                             
                        
                            Registration costs
                            
                            
                        


                        
                    
                
            
            

                
                    
                        
                        Taxes &amp; Depreciation
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Your state &amp; federal tax rate 
                            i
                        
                        Must be between 0.00% and 90.00%
                        
                            $
                            
                            %
                             
                        
                            Your state &amp; federal tax rate
                            
                            
                        


                        
                    
                    
                        
                            Future depreciation 
                            i
                        
                        Error message for dropdown list here
                        
                            
                                Low

                                Average

                                High
                            


                        
                    
                    
                        
                            Your savings interest rate
                                
                        
                        Must be between 0.00% and 100.00%
                        
                            $
                            
                            %
                             
                        
                            Your savings interest rate
                            
                            
                        


                        
                    
                
            
        

                
            

            
                
                These calculators are intended to be used by Leadfusion for demonstration purposes only. Linking and usage of these calculators without a written license agreement is in violation of the Leadfusion Terms of Use and therefore not permitted. Please Click Here to go to GOOGLE. Please contact cs@leadfusion.com with any questions.
            

            
                
                    
                        
                            
                            Terms of use
                        
                    
                    © 2025 Leadfusion
                
            
        ' or . = '
            
            
            
            
                
                    Results
                    
                        The used vehicle will cost you $89,910 less on average each year
                    
                    
                        The used vehicle will cost you $89,910 less on average each year

                        Show details

                        

                            
                                Results details
                                    
                                    
                                        
                                        New
                                        Used
                                    
                                    
                                    

                                    
                                        
                                            Average cost per year
                                        
                                        $93,103
                                        
                                        $3,193
                                        
                                    
                                    
                                        
                                            Monthly payment
                                        
                                        $20,039
                                        
                                        $233
                                        
                                    
                                    
                                        
                                            Total interest paid
                                        
                                        $73,563
                                        
                                        $1,163
                                        
                                    
                                    
                                        
                                            Total depreciation
                                        
                                        $375,379
                                        
                                        $5,588
                                        
                                    
                                    
                                
                            
                            
                                {{#text}}
                                &lt;div id=&quot;lf_answer_text&quot; class=&quot;tableCellADA&quot;>{{{text}}}&lt;/div>
                                {{/text}}
                                {{^text}}
                                {{#.}}
                                {{#title}}
                                &lt;div id=&quot;lf_result_title&quot; class=&quot;lf-result-title&quot;>{{{title}}}&lt;/div>
                                {{/title}}
                                &lt;div class=&quot;lf-results-list lf-list-{{listNumber}}&quot;>
                                    {{#entries}}
                                    &lt;div id=&quot;lf_{{dataElementName}}_results&quot; class=&quot;lf-result-list-content&quot;>
                                        &lt;div id=&quot;lf_{{dataElementName}}_result_label&quot; class=&quot;lf-details-list-label&quot;>
                                            {{{fieldLabel}}}
                                        &lt;/div>
                                        {{#formattedValues}}
                                        &lt;div id=&quot;lf_{{dataElementName}}_result_value&quot;
                                             class=&quot;lf-details-list-value&quot;>{{value}}
                                        &lt;/div>
                                        {{/formattedValues}}
                                    &lt;/div>
                                    {{/entries}}
                                &lt;/div>
                                {{/.}}
                                {{/text}}
                            
                            
                                {{#text}}
                                &lt;div id=&quot;lf_answer_text&quot; class=&quot;tableCellADA&quot;>{{{text}}}&lt;/div>
                                {{/text}}
                                {{^text}}
                                {{#.}}
                                {{#title}}
                                &lt;div id=&quot;lf_result_title&quot; class=&quot;tableCellADA&quot; class=&quot;lf-result-title&quot;>{{{title}}}&lt;/div>
                                {{/title}}
                                &lt;table class=&quot;lf-results-table&quot; id=&quot;lf-results-table-main&quot;>
                                    &lt;thead>
                                    &lt;tr>
                                        {{#columnHeaders}}
                                        &lt;td>&lt;/td>
                                        {{#columnHeader}}
                                        &lt;th class=&quot;lf-results-header tableCellADA&quot; scope=&quot;col&quot;>{{.}}&lt;/th>
                                        {{/columnHeader}}
                                        {{/columnHeaders}}
                                    &lt;/tr>
                                    &lt;/thead>
                                    &lt;tbody>
                                    {{#subtitle}}
                                    &lt;tr>
                                        &lt;th id=&quot;lf_result_subtitle1&quot;
                                            class=&quot;lf-result-subtitle tableCellADA three-headers&quot; colspan=&quot;100%&quot;>
                                            {{{subtitle}}}
                                        &lt;/th>
                                    &lt;/tr>
                                    {{/subtitle}}

                                    {{#entries}}
                                    &lt;tr id=&quot;lf_{{dataElementName}}_results&quot;>
                                        &lt;th id=&quot;lf_{{dataElementName}}_result_label&quot; class=&quot;tableCellADA&quot; scope=&quot;row&quot;>
                                            {{#negLabel}}&lt;span class=&quot;lf-minus-sign&quot;>&amp;minus;&lt;/span>{{/negLabel}}{{{fieldLabel}}}
                                            {{#glossaryTerm}}
                                            &lt;a role=&quot;button&quot; href=&quot;#{{glossaryTerm}}&quot;
                                               id=&quot;lf_{{dataElementName}}_tooltip&quot;
                                               class=&quot;lf-show-tooltip lf_{{dataElementName}}_tooltip lf-more-details-tooltip&quot;
                                               {{#tooltipAriaLabel}}aria-label=&quot;{{{tooltipAriaLabel}}}&quot;{{/tooltipAriaLabel}}>
                                            &lt;div aria-hidden=&quot;true&quot; class=&quot;lf-tooltip-i lf-results-tooltip&quot;>i&lt;/div>
                                            &lt;/a>
                                            {{/glossaryTerm}}
                                        &lt;/th>
                                        {{#formattedValues}}
                                        &lt;td id=&quot;lf_{{dataElementName}}_result_value&quot;
                                            class=&quot;tableCellADA {{shownClasses}}&quot;>&lt;span class=&quot;lf-nbsp-number&quot;>{{#negSymbol}}&lt;span class=&quot;lf-minus-sign {{negSymbolClass}}&quot;>&amp;minus;&lt;/span>{{/negSymbol}}{{value}}&lt;/span>
                                        &lt;/td>
                                        {{/formattedValues}}
                                    &lt;/tr>
                                    {{/entries}}
                                    &lt;/tbody>
                                &lt;/table>
                                {{/.}}
                                {{/text}}
                            
                        

                    
                    Next Steps

                    
                        Enable color patterns
                            
                                Average cost per year of the new vehicle and the used vehicleBar chart with 2 data series.View as data table, Average cost per year of the new vehicle and the used vehicleThe chart has 1 X axis displaying categories. The chart has 1 Y axis displaying values. Data ranges from 3193 to 93103.Chart context menuAverage cost per year of the new vehicle and the used vehicleNewUsed0100k25k50k75kEnd of interactive chart.
                            
                            
                                
                            
                        
                        Change views between Average Cost graph, and Payments graph
                        
                            
                            
                                
                                Average Cost
                            
                            
                                
                                Payments
                            
                        
                    
                    
                        &lt;div class=&quot;lf-graph-list&quot;>
                            {{#.}}
                            {{^table}}
                            &lt;div id=&quot;{{id}}-graph-container&quot; class=&quot;lf-graph{{#first}} lf-active{{/first}}&quot; role=&quot;tabpanel&quot; aria-labelledby=&quot;lf_graph_tabs_{{id}}&quot;>
                                &lt;div id=&quot;{{id}}&quot; class=&quot;lf-cm-90 lf-graph_container&quot;>&lt;/div>
                            &lt;/div>
                            {{/table}}
                            {{#table}}
                            &lt;div id=&quot;{{id}}-graph-container&quot; class=&quot;lf-graph lf-hidden&quot; role=&quot;tabpanel&quot; aria-labelledby=&quot;lf_graph_tabs_{{id}}&quot;>
                                &lt;div class=&quot;lf-tables-links&quot;>
                                    {{#tables}}
                                    &lt;div>
                                        {{#label}}
                                        &lt;span class=&quot;lf-graph-label lf-graph-label-multiple&quot;>{{title}}&lt;/span>
                                        &lt;button id=&quot;lf_tables_links{{index}}&quot;
                                                class=&quot;lf-open-table lf-open-table-xl lf-open-table-multiple&quot;
                                                tabletitle=&quot;{{title}}&quot;
                                                lf-index=&quot;{{index}}&quot;
                                                lf-title=&quot;{{title}}&quot;>
                                            &lt;span aria-hidden=&quot;true&quot; class=&quot;lf-open-table-label&quot;>&lt;/span>
                                        &lt;/button>
                                        {{/label}}
                                        {{^label}}
                                        &lt;span class=&quot;lf-graph-label lf-graph-label-single&quot;>{{title}}&lt;/span>
                                        &lt;button id=&quot;lf_tables_links{{index}}&quot;
                                                class=&quot;lf-open-table lf-open-table-xl lf-open-table-single&quot;
                                                tabletitle=&quot;{{title}}&quot;
                                                lf-index=&quot;{{index}}&quot;
                                                lf-title=&quot;{{title}}&quot;>
                                            &lt;span aria-hidden=&quot;true&quot; class=&quot;lf-open-table-label&quot;>&lt;/span>
                                        &lt;/button>
                                        {{/label}}
                                    &lt;/div>
                                    {{/tables}}
                                &lt;/div>
                            &lt;/div>
                            {{/table}}
                            {{/.}}
                        &lt;/div>
                        &lt;div id=&quot;lf_selectors_description&quot; class=&quot;visually-hidden&quot;>&lt;/div>
                        &lt;div class=&quot;lf-results-loop&quot; role=&quot;tablist&quot; aria-labelledby=&quot;lf_selectors_description&quot;>
                            &lt;span class=&quot;lf-divider&quot;>&lt;/span>
                            {{#.}}
                            &lt;button id=&quot;lf_graph_tabs_{{id}}&quot; class=&quot;{{#first}}lf-active {{/first}}{{#last}}lf-last-loop{{/last}} lf-graph-tabs&quot; role=&quot;tab&quot; aria-selected=&quot;false&quot; aria-controls=&quot;{{id}}-graph-container&quot; tabindex=&quot;-1&quot;>
                                &lt;span class=&quot;lf-dot&quot; aria-hidden=&quot;true&quot;>&lt;/span>
                                &lt;div class=&quot;lf-dot-holder lf-dot-label&quot;>{{label}}&lt;/div>
                            &lt;/button>
                            {{/.}}
                        &lt;/div>
                    
                    
                        
                    
                

                
                
                    These calculators are intended to be used by Leadfusion for demonstration purposes only. Linking and usage of these calculators without a written license agreement is in violation of the Leadfusion Terms of Use and therefore not permitted. Please Click Here to go to GOOGLE. Please contact cs@leadfusion.com with any questions.
                

            

            
                Inputs
                A note about this tool: Some of the fields are pre-filled to get you started. Move the sliders or enter a number to change the amounts. The calculator results will automatically update as you move the sliders or leave a text field.
                
                
                    
    
            

                
                    
                        
                        New Vehicle Loan
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Purchase price
                                
                        
                        Must be between $0 and $1,000,000
                        
                            $
                            
                            %
                             
                        
                            Purchase price
                            
                            
                        


                        
                    
                    
                        
                            Down payment 
                            i
                        
                        Must be between $0 and $249,999
                        
                            $
                            
                            %
                             
                        
                            Down payment
                            
                            
                        


                        
                    
                    
                        
                            Interest rate
                                
                        
                        Must be between 0.00% and 50.00%
                        
                            $
                            
                            %
                             
                        
                            Interest rate
                            
                            
                        


                        
                    
                    
                        
                            Loan term (months)
                            i
                        
                        Must be between 1 and 360
                        
                            $
                            
                            %
                             
                        
                            Loan term
                            
                            
                        


                        
                    
                
            
            

                
                    
                        
                        New Vehicle Cost
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Years you will own vehicle
                                
                        
                        Must be between 1 and 30
                        
                            $
                            
                            %
                             
                        
                            Years you will own vehicle
                            
                            
                        


                        
                    
                    
                        
                            Monthly maintenance costs
                                
                        
                        Must be between $0 and $10,000
                        
                            $
                            
                            %
                             
                        
                            Monthly maintenance costs
                            
                            
                        


                        
                    
                    
                        
                            Monthly insurance costs
                                
                        
                        Must be between $0 and $10,000
                        
                            $
                            
                            %
                             
                        
                            Monthly insurance costs
                            
                            
                        


                        
                    
                    
                        
                            Registration costs
                                
                        
                        Must be between $0 and $100,000
                        
                            $
                            
                            %
                             
                        
                            Registration costs
                            
                            
                        


                        
                    
                
            
            

                
                    
                        
                        Used Vehicle Loan
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Purchase price
                                
                        
                        Must be between $0 and $250,000
                        
                            $
                            
                            %
                             
                        
                            Purchase price
                            
                            
                        


                        
                    
                    
                        
                            Down payment 
                            i
                        
                        Must be between $0 and $249,999
                        
                            $
                            
                            %
                             
                        
                            Down payment
                            
                            
                        


                        
                    
                    
                        
                            Interest rate
                                
                        
                        Must be between 0.00% and 50.00%
                        
                            $
                            
                            %
                             
                        
                            Interest rate
                            
                            
                        


                        
                    
                    
                        
                            Loan term  (months)
                            i
                        
                        Must be between 1 and 360
                        
                            $
                            
                            %
                             
                        
                            Loan term 
                            
                            
                        


                        
                    
                
            
            

                
                    
                        
                        Used Vehicle Cost
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Vehicle age
                                 (years)
                        
                        Must be between 0 and 30
                        
                            $
                            
                            %
                             
                        
                            Vehicle age
                            
                            
                        


                        
                    
                    
                        
                            Years you will own vehicle
                                
                        
                        Must be between 1 and 30
                        
                            $
                            
                            %
                             
                        
                            Years you will own vehicle
                            
                            
                        


                        
                    
                    
                        
                            Monthly maintenance costs
                                
                        
                        Must be between $0 and $10,000
                        
                            $
                            
                            %
                             
                        
                            Monthly maintenance costs
                            
                            
                        


                        
                    
                    
                        
                            Monthly insurance costs
                                
                        
                        Must be between $0 and $10,000
                        
                            $
                            
                            %
                             
                        
                            Monthly insurance costs
                            
                            
                        


                        
                    
                    
                        
                            Registration costs
                                
                        
                        Must be between $0 and $100,000
                        
                            $
                            
                            %
                             
                        
                            Registration costs
                            
                            
                        


                        
                    
                
            
            

                
                    
                        
                        Taxes &amp; Depreciation
                    
                

                
                    
                        
                            
                        
                    
                
                
                    
                        
                            Your state &amp; federal tax rate 
                            i
                        
                        Must be between 0.00% and 90.00%
                        
                            $
                            
                            %
                             
                        
                            Your state &amp; federal tax rate
                            
                            
                        


                        
                    
                    
                        
                            Future depreciation 
                            i
                        
                        Error message for dropdown list here
                        
                            
                                Low

                                Average

                                High
                            


                        
                    
                    
                        
                            Your savings interest rate
                                
                        
                        Must be between 0.00% and 100.00%
                        
                            $
                            
                            %
                             
                        
                            Your savings interest rate
                            
                            
                        


                        
                    
                
            
        

                
            

            
                
                These calculators are intended to be used by Leadfusion for demonstration purposes only. Linking and usage of these calculators without a written license agreement is in violation of the Leadfusion Terms of Use and therefore not permitted. Please Click Here to go to GOOGLE. Please contact cs@leadfusion.com with any questions.
            

            
                
                    
                        
                            
                            Terms of use
                        
                    
                    © 2025 Leadfusion
                
            
        ')]</value>
      <webElementGuid>376dd1de-b388-4f47-b56a-dbb6f813d312</webElementGuid>
   </webElementXpaths>
</WebElementEntity>
