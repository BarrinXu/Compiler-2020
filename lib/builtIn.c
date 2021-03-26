#include <stdio.h>
#include <string.h>
#include <stdlib.h>
void BI_print(char *str)
{
    printf("%s",str);
}

void BI_println(char *str)
{
    printf("%s\n",str);
}

void BI_printInt(int n)
{
    printf("%d",n);
}

void BI_printlnInt(int n)
{
    printf("%d\n",n);
}

char* BI_getString()
{
    char *tmp=malloc(sizeof(char)*1000);
    scanf("%s",tmp);
    return tmp;
}

int BI_getInt()
{
    int tmp;
    scanf("%d",&tmp);
    return tmp;
}

char* BI_toString(int i)
{
    char *tmp=malloc(sizeof(char)*12);
    sprintf(tmp,"%d",i);
    return tmp;
}

int BI_str_length(char *str)
{
    return strlen(str);
}

char* BI_str_substring(char *str,int left,int right)
{
    char *tmp=malloc(right-left+1);
    memcpy(tmp,str+left,right-left);
    tmp[right-left]='\0';
    return tmp;
}

int BI_str_parseInt(char *str)
{
    int tmp;
    sscanf(str,"%d",&tmp);
    return tmp;
}

int BI_str_ord(char *str,int pos)
{
    return str[pos];
}

char* BI_str_add(char *a,char *b)
{
    int lena=strlen(a),lenb=strlen(b);
    char *tmp=malloc(lena+lenb+1);
    memcpy(tmp,a,lena);
    memcpy(tmp+lena,b,lenb);
    tmp[lena+lenb]='\0';
    return tmp;
}

int BI_str_EQ(char *a,char *b)
{
    return !strcmp(a,b);
}

int BI_str_NEQ(char *a,char *b)
{
    return strcmp(a,b);
}

int BI_str_LT(char *a,char *b)
{
    return strcmp(a,b)<0;
}

int BI_str_GT(char *a,char *b)
{
    return strcmp(a,b)>0;
}

int BI_str_LE(char *a,char *b)
{
    return strcmp(a,b)<=0;
}

int BI_str_GE(char *a,char *b)
{
    return strcmp(a,b)>=0;
}
